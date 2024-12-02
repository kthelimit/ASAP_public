package sky.project.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.CurrentStatus;

public interface OrderService {

    void registerOrder(OrdersDTO ordesrDTO);

    // 검색 및 페이징 처리
    Page<OrdersDTO> searchOrders(String keyword, Pageable pageable);

    // 모든 발주 목록 조회
    Page<OrdersDTO> getAllOrders(Pageable pageable);

    Page<OrdersDTO> findOrdersBySupplier(String supplierName, Pageable pageable);

    void updateOrderStatus(Long orderId, CurrentStatus status);

    Page<OrdersDTO> findByMaterialTypeAndStatus(String materialType, String status, Pageable pageable);

    Page<OrdersDTO> findByStatus(String status, Pageable pageable);

    //대시 보드 출력용 이번달 발주 건 수
    int getCountOrderThisMonth();

    int getCountOrderBySupplier(String supplierName);

    OrdersDTO processDeliveryRequest(Long orderId, int requestQuantity);



}
