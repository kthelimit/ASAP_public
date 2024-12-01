package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Material;
import sky.project.Entity.Order;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.OrderRepository;
import sky.project.Service.OrderService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public void registerOrder(OrdersDTO ordersDTO) {
        // DTO -> 엔티티 변환 및 저장
        Order order = toEntity(ordersDTO);
        //발주서 코드 설정
        order.setOrderCode(generateOrderCode(ordersDTO));

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
    public Page<OrdersDTO> findOrdersBySupplier(String supplierName, Pageable pageable) {
        // 특정 공급사의 주문 조회
        return orderRepository.findBySupplierName(supplierName, pageable)
                .map(this::toDTO);
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

    // Order -> OrdersDTO 변환
    private OrdersDTO toDTO(Order order) {
        if (order == null) return null;

        // MaterialType 조회
        String materialType = materialRepository.findFirstByMaterialName(order.getMaterialName())
                .map(Material::getMaterialType)
                .orElse("정보 없음");

        // 엔티티 -> DTO 변환
        return OrdersDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderCode(order.getOrderCode())
                .expectedDate(order.getExpectedDate())
                .procurePlanCode(order.getProcurePlanCode())
                .supplierName(order.getSupplierName())
                .materialName(order.getMaterialName())
                .orderQuantity(order.getOrderQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus() != null ? order.getStatus().name() : CurrentStatus.ON_HOLD.name()) // 기본값 설정
                .materialType(materialType)
                .build();
    }

    // OrdersDTO -> Order 변환
    private Order toEntity(OrdersDTO dto) {
        if (dto == null) return null;

        // DTO -> 엔티티 변환
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderCode(dto.getOrderCode());
        order.setOrderDate(dto.getOrderDate());
        order.setExpectedDate(dto.getExpectedDate());
        order.setProcurePlanCode(dto.getProcurePlanCode());
        order.setSupplierName(dto.getSupplierName());
        order.setMaterialName(dto.getMaterialName());
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


}
