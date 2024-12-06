package sky.project.ServiceImpl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.OrderService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierStockRepository supplierStockRepository;

    @Autowired
    private ProcurementPlanRepository procurementPlanRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Override
    public void registerOrder(OrdersDTO ordersDTO) {
        // DTO -> 엔티티 변환 및 저장
        Order order = toEntity(ordersDTO);

        //발주서 코드 설정
        order.setOrderCode(generateOrderCode(ordersDTO));

        //조달계획 상태 변경(ON_HOLD->IN_PROGRESS)
        ProcurementPlan plan = procurementPlanRepository.findByProcurePlanCode(ordersDTO.getProcurePlanCode());
        plan.setStatus(CurrentStatus.IN_PROGRESS);
        procurementPlanRepository.save(plan);

        order.setStatus(CurrentStatus.ON_HOLD); // 기본 상태 설정
        orderRepository.save(order);
    }

    @Override
    public Page<OrdersDTO> searchOrders(String keyword, Pageable pageable) {
        // 공급사 이름 또는 자재 이름으로 검색
        return orderRepository.findBySupplierNameContainingOrMaterialNameContaining(keyword, keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<OrdersDTO> getAllOrders(Pageable pageable) {
        // 모든 주문 조회
        return orderRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public OrdersDTO findByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found with code: " + orderCode));
        // Order -> OrdersDTO 변환
        return toDTO(order);
    }


    @Override
    public Page<OrdersDTO> findOrdersBySupplier(String supplierName, Pageable pageable) {
        // 특정 공급사의 주문 조회
        return orderRepository.findBySupplierName(supplierName, pageable)
                .map(this::toDTO);
    }

    @Transactional
    @Override
    public OrdersDTO processDeliveryRequest(Long orderId, int requestedQuantity) {
        // 엔티티 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // DTO를 생성하여 작업 (엔티티 값이 없으면 계산)
        OrdersDTO orderDTO = toDTO(order);

        // 요청 가능 수량 및 필요 수량 확인
        if (requestedQuantity > orderDTO.getAvailableStock()) {
            throw new IllegalArgumentException("요청 수량이 요청 가능 수량을 초과할 수 없습니다.");
        }
        if (requestedQuantity > orderDTO.getRemainedQuantity()) {
            throw new IllegalArgumentException("요청 수량이 남은 발주량을 초과할 수 없습니다.");
        }


        // 변경된 엔티티 저장
        orderRepository.save(order);

        // 갱신된 엔티티를 기반으로 DTO 반환
        return toDTO(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, CurrentStatus status) {
        // 주문 상태 업데이트
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Page<OrdersDTO> findByMaterialTypeAndStatus(String materialType, String status, Pageable pageable) {
        // String을 CurrentStatus로 변환
        CurrentStatus currentStatus = CurrentStatus.valueOf(status.toUpperCase());

        // Repository 호출
        Page<Order> orders = orderRepository.findByMaterialTypeAndStatus(materialType, currentStatus, pageable);

        // 엔티티를 DTO로 변환
        return orders.map(order -> toDTO(order));
    }


    @Override
    public Page<OrdersDTO> findByStatus(String status, Pageable pageable) {
        // String을 CurrentStatus로 변환
        CurrentStatus currentStatus = CurrentStatus.valueOf(status.toUpperCase());

        // Repository 호출
        Page<Order> orders = orderRepository.findByStatus(currentStatus, pageable);

        // 엔티티를 DTO로 변환
        return orders.map(order -> toDTO(order));
    }


    private OrdersDTO toDTO(Order order) {
        if (order == null) return null;

        // 남은 조달 수량 ( 발주량 - 현재 납품지시 넣은 수량)
        int remainedQuantity = order.getOrderQuantity();
        List<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findDeliveryRequestsByOrderCode(order.getOrderCode());
        int sumOfRequests = 0;
        for (DeliveryRequest deliveryRequest : deliveryRequests) {
            sumOfRequests += deliveryRequest.getRequestedQuantity();
        }
        remainedQuantity -= sumOfRequests;

        // 상태 업데이트
        if (remainedQuantity == 0) {
            order.setStatus(CurrentStatus.DELIVERED);
        }

        //업체의 가용 재고
        int availableStock = calculateAvailableStock(order);

        //검수 횟수
        int finishedInspection = inspectionRepository.countByOrderCodeWithFinished(order.getOrderCode());
        int totalInspection = inspectionRepository.countByOrderCode(order.getOrderCode());


        //현재 납품지시 중이고 아직 도착하지 않은 수량
        List<DeliveryRequest> deliveryRequestsOnHOLD = deliveryRequestRepository.findDeliveryRequestsByOrderCodeInProgress(order.getOrderCode());
        int deliveryQuantity=0;
        for(DeliveryRequest deliveryRequest : deliveryRequestsOnHOLD) {
            deliveryQuantity += deliveryRequest.getRequestedQuantity();
        }

        return OrdersDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderCode(order.getOrderCode())
                .expectedDate(order.getExpectedDate())
                .procurePlanCode(order.getProcurePlanCode())
                .supplierName(order.getSupplier().getSupplierName())
                .materialName(order.getMaterial().getMaterialName())
                .orderQuantity(order.getOrderQuantity())
                .remainedQuantity(remainedQuantity) //남은 수량
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().name() : CurrentStatus.ON_HOLD.name())
                .materialType(order.getMaterial().getMaterialType())
                .availableStock(availableStock) // 요청 가능 수량
                .deliveryQuantity(deliveryQuantity)
                .finishedInspection(finishedInspection)
                .totalInspection(totalInspection)
                .build();
    }

    // 추가: `availableStock` 계산 로직
    private int calculateAvailableStock(Order order) {

        List<CurrentStatus> statuses = List.of(CurrentStatus.APPROVAL, CurrentStatus.IN_PROGRESS, CurrentStatus.FINISHED);

        //승인된 발주수량?
        int totalApprovedQuantity = orderRepository.findApprovedQuantity(
                order.getSupplier().getSupplierName(),
                order.getMaterial().getMaterialName(),
                statuses
        ) - order.getOrderQuantity();


        int totalStock = supplierStockRepository.findBySupplier_SupplierNameAndMaterial_MaterialName(
                        order.getSupplier().getSupplierName(), order.getMaterial().getMaterialName())
                .map(SupplierStock::getStock)
                .orElse(0);

        return totalStock - totalApprovedQuantity;
    }

    // OrdersDTO -> Order 변환
    private Order toEntity(OrdersDTO dto) {
        if (dto == null) return null;
        Supplier supplier = supplierRepository.findBySupplierName(dto.getSupplierName());
        Material material = materialRepository.findByMaterialName(dto.getMaterialName()).isEmpty() ? null : materialRepository.findByMaterialName(dto.getMaterialName()).get(0);

        // DTO -> 엔티티 변환
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderCode(dto.getOrderCode());
        order.setOrderDate(dto.getOrderDate());
        order.setExpectedDate(dto.getExpectedDate());
        order.setProcurePlanCode(dto.getProcurePlanCode());
        order.setSupplier(supplier);
        order.setMaterial(material);
        order.setOrderQuantity(dto.getOrderQuantity());
        order.setTotalPrice(dto.getTotalPrice());
        order.setStatus(dto.getStatus() != null ? CurrentStatus.valueOf(dto.getStatus().toUpperCase()) : CurrentStatus.ON_HOLD); // String -> Enum 변환
        return order;
    }

    private String generateOrderCode(OrdersDTO dto) {
        // 매터리얼 코드에 따른 접두어 설정
        String prefix = "ORD";
        Material material = materialRepository.findByMaterialName(dto.getMaterialName()).get(0);
        switch (material.getMaterialCode().substring(3, 5)) {
            case "WH":
                prefix += "WH";
                break;
            case "RI":
                prefix += "RI";
                break;
            case "HA":
                prefix += "HA";
                break;
            case "SA":
                prefix += "SA";
                break;
            case "PE":
                prefix += "PE";
                break;
            case "BO":
                prefix += "BO";
                break;
            case "B1":
                prefix += "B1";
                break;
            case "B2":
                prefix += "B2";
                break;
            case "B3":
                prefix += "B3";
                break;
            case "K1":
                prefix += "K1";
                break;
            case "K2":
                prefix += "K2";
                break;
            default:
                prefix += "UN";
                break;
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        LocalDateTime today = LocalDateTime.now();
        String dateCode = today.format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = orderRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }


    //대시 보드 출력용 이번달 발주 건 수
    @Override
    public int getCountOrderThisMonth() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return orderRepository.countOrderThisMonth(start, end);
    }

    //대시보드 출력용 업체에 들어온 발주 건수
    @Override
    public int getCountOrderBySupplierThisMonth(String supplierName) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return orderRepository.countOrderBySupplierName(supplierName, start, end);
    }

    //대시보드 출력용 업체에 들어온 새 발주 건수(승인 전)
    @Override
    public int getCountOrderBySupplierOnHOLD(String supplierName) {
        return orderRepository.countOrderBySupplierNameOnHOLD(supplierName);
    }

    //대시보드 출력용 최근 발주리스트
    @Override
    public List<OrdersDTO> getRecentOrderList() {
        return orderRepository.findRecentOrder().stream().map(this::toDTO).toList();
    }

    //대시보드 출력용 최근 발주리스트(업체에 들어온 것)
    @Override
    public List<OrdersDTO> getRecentOrderListForSupplier(String supplierName) {
        return orderRepository.findRecentOrderForSupplier(supplierName).stream().map(this::toDTO).toList();
    }

    @Override
    public Page<OrdersDTO> findByStatuses(List<String> statuses, Pageable pageable) {
        // String 리스트를 CurrentStatus 리스트로 변환
        List<CurrentStatus> currentStatuses = statuses.stream()
                .map(status -> CurrentStatus.valueOf(status.toUpperCase()))
                .toList();

        // Repository 호출
        Page<Order> orders = orderRepository.findByStatuses(currentStatuses, pageable);

        // 엔티티 -> DTO 변환
        return orders.map(this::toDTO);
    }

    public List<OrdersDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
