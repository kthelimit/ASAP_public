package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.DeliveryRequestDTO;
import sky.project.Entity.DeliveryRequest;


public interface DeliveryRequestService {
    DeliveryRequestDTO createRequest(Long orderId, int requestedQuantity); // 요청 생성
     // 공급사 이름으로 요청 조회
    void updateRequestStatus(Long id, String status);              // 요청 상태 변경
    Page<DeliveryRequestDTO> findAll(Pageable pageable);
    Page<DeliveryRequestDTO> findRequestsBySupplier(String supplierName, Pageable pageable);
    DeliveryRequest findById(Long id);
}
