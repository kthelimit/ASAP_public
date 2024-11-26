package sky.project.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.OrdersDTO;

public interface OrderService {

    void registerOrder(OrdersDTO ordesrDTO);

    // 검색 및 페이징 처리
    Page<OrdersDTO> searchOrders(String keyword, Pageable pageable);

    // 모든 발주 목록 조회
    Page<OrdersDTO> getAllOrders(Pageable pageable);

}
