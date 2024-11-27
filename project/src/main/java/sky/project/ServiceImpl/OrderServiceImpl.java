package sky.project.ServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.Repository.OrderRepository;
import sky.project.Service.OrderService;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void registerOrder(OrdersDTO ordersDTO) {
        // DTO를 엔티티로 변환
        Order order = toEntity(ordersDTO);

        // 엔티티 저장
        orderRepository.save(order);
    }

    @Override
    public Page<OrdersDTO> searchOrders(String keyword, Pageable pageable) {
        Page<Order> order = orderRepository. findBySupplierNameContainingOrMaterialNameContaining(
                 keyword, keyword, pageable);
        return order.map(this::toDTO);
    }

    @Override
    public Page<OrdersDTO> getAllOrders(Pageable pageable) {
        Page<Order> order = orderRepository.findAll(pageable);
        System.out.println("Fetched orders from DB: " + order.getContent());
        return order.map(this::toDTO);
    }


    // Order -> OrderDTO 변환
    private OrdersDTO toDTO(Order order) {
        if (order == null) return null;

        OrdersDTO dto = new OrdersDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setExpectedDate(order.getExpectedDate());
        dto.setProcurePlanCode(order.getProcurePlanCode());
        dto.setSupplierName(order.getSupplierName());
        dto.setMaterialName(order.getMaterialName());
        dto.setOrderQuantity(order.getOrderQuantity());
        dto.setTotalPrice(order.getTotalprice()); // DTO와 엔티티 필드 매핑
        dto.setStatus(order.getStatus());
        return dto;
    }

    // OrderDTO -> Order 변환
    private Order toEntity(OrdersDTO dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderDate(dto.getOrderDate());
        order.setExpectedDate(dto.getExpectedDate());
        order.setProcurePlanCode(dto.getProcurePlanCode());
        order.setSupplierName(dto.getSupplierName());
        order.setMaterialName(dto.getMaterialName());
        order.setOrderQuantity(dto.getOrderQuantity());
        order.setTotalprice(dto.getTotalPrice()); // DTO와 엔티티 필드 매핑
        order.setStatus(dto.getStatus());
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
