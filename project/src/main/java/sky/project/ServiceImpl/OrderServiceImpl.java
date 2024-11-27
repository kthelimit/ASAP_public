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

import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
                .expectedDate(order.getExpectedDate())
                .procurePlanCode(order.getProcurePlanCode())
                .supplierName(order.getSupplierName())
                .materialName(order.getMaterialName())
                .orderQuantity(order.getOrderQuantity())
                .totalPrice(order.getTotalprice())
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
    public int getCountOrderThisMonth(){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return orderRepository.countOrderThisMonth(start, end);
    }


}
