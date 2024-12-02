package sky.project.ServiceImpl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Material;
import sky.project.Entity.Order;
import sky.project.Entity.SupplierStock;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.OrderRepository;
import sky.project.Repository.SupplierStockRepository;
import sky.project.Service.OrderService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierStockRepository supplierStockRepository;

    @Override
    public void registerOrder(OrdersDTO ordersDTO) {
        // DTO -> 엔티티 변환 및 저장
        Order order = toEntity(ordersDTO);
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
        if (requestedQuantity > orderDTO.getRequiredQuantity()) {
            throw new IllegalArgumentException("요청 수량이 필요 조달 수량을 초과할 수 없습니다.");
        }

        // 값 업데이트
        int newAvailableStock = orderDTO.getAvailableStock() - requestedQuantity;
        int newRequiredQuantity = orderDTO.getRequiredQuantity() - requestedQuantity;

        // 엔티티 업데이트 (DB에 반영)
        order.setAvailableStock(newAvailableStock);
        order.setRequiredQuantity(newRequiredQuantity);

        // 상태 업데이트
        if (newRequiredQuantity == 0) {
            order.setStatus(CurrentStatus.FINISHED);
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

        // 기존 엔티티 값 사용 또는 계산
        int requiredQuantity = order.getRequiredQuantity() != null ? order.getRequiredQuantity() : order.getOrderQuantity();
        int availableStock = order.getAvailableStock() != null ? order.getAvailableStock() : calculateAvailableStock(order);

        String materialType = materialRepository.findFirstByMaterialName(order.getMaterialName())
                .map(Material::getMaterialType)
                .orElse("정보 없음");

        return OrdersDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .expectedDate(order.getExpectedDate())
                .procurePlanCode(order.getProcurePlanCode())
                .supplierName(order.getSupplierName())
                .materialName(order.getMaterialName())
                .orderQuantity(order.getOrderQuantity())
                .requiredQuantity(requiredQuantity) //필요 조달 수량
                .totalPrice(order.getTotalprice())
                .status(order.getStatus() != null ? order.getStatus().name() : CurrentStatus.ON_HOLD.name())
                .materialType(materialType)
                .availableStock(availableStock) // 요청 가능 수량
                .build();
    }

    // 추가: `availableStock` 계산 로직
    private int calculateAvailableStock(Order order) {

        List<CurrentStatus> statuses = List.of(CurrentStatus.APPROVAL, CurrentStatus.IN_PROGRESS, CurrentStatus.FINISHED);


        int totalApprovedQuantity = orderRepository.findApprovedQuantity(
                order.getSupplierName(),
                order.getMaterialName(),
                statuses
        )-order.getOrderQuantity();

        int totalStock = supplierStockRepository.findBySupplier_SupplierNameAndMaterial_MaterialName(
                        order.getSupplierName(), order.getMaterialName())
                .map(SupplierStock::getStock)
                .orElse(0);

        return totalStock - totalApprovedQuantity;
    }

    // OrdersDTO -> Order 변환
    private Order toEntity(OrdersDTO dto) {
        if (dto == null) return null;

        // DTO -> 엔티티 변환
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderDate(dto.getOrderDate());
        order.setExpectedDate(dto.getExpectedDate());
        order.setProcurePlanCode(dto.getProcurePlanCode());
        order.setSupplierName(dto.getSupplierName());
        order.setMaterialName(dto.getMaterialName());
        order.setOrderQuantity(dto.getOrderQuantity());
        order.setTotalprice(dto.getTotalPrice());
        order.setStatus(dto.getStatus() != null ? CurrentStatus.valueOf(dto.getStatus().toUpperCase()) : CurrentStatus.ON_HOLD); // String -> Enum 변환
        return order;
    }

    //대시 보드 출력용 이번달 발주 건 수
    @Override
    public int getCountOrderThisMonth() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return orderRepository.countOrderThisMonth(start, end);
    }

    @Override
    public int getCountOrderBySupplier(String supplierName) {
        return orderRepository.countOrderBySupplierName(supplierName);
    }

}
