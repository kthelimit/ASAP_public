package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.DeliveryRequestDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.DeliveryRequest;
import sky.project.Entity.Order;
import sky.project.Repository.DeliveryRequestRepository;
import sky.project.Repository.OrderRepository;
import sky.project.Service.DeliveryRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public DeliveryRequestDTO createRequest(Long orderId, int requestedQuantity) {
        // 주문 엔티티 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // DeliveryRequest 생성 및 저장
        DeliveryRequest deliveryRequest = DeliveryRequest.builder()
                .order(order)
                .supplierName(order.getSupplierName())
                .materialName(order.getMaterialName())
                .requestedQuantity(requestedQuantity)
                .requestedAt(LocalDateTime.now())
                .status(CurrentStatus.IN_PROGRESS) // 기본 상태 설정
                .build();

        DeliveryRequest savedRequest = deliveryRequestRepository.save(deliveryRequest);

        return toDTO(savedRequest);
    }

    @Override
    public Page<DeliveryRequestDTO> findAll(Pageable pageable) {
        return deliveryRequestRepository.findAll(pageable)
                .map(this::toDTO); // Entity를 DTO로 변환
    }

    @Override
    public Page<DeliveryRequestDTO> findRequestsBySupplier(String supplierName, Pageable pageable) {
        return deliveryRequestRepository.findBySupplierName(supplierName, pageable)
                .map(this::toDTO);
    }




    @Override
    public void updateRequestStatus(Long id, String status) {
        // DeliveryRequest 엔티티 조회
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery request not found"));

        // 상태 업데이트
        deliveryRequest.setStatus(CurrentStatus.valueOf(status.toUpperCase()));
        deliveryRequestRepository.save(deliveryRequest);
    }

    private DeliveryRequestDTO toDTO(DeliveryRequest deliveryRequest) {
        return DeliveryRequestDTO.builder()
                .id(deliveryRequest.getId())
                .orderCode(deliveryRequest.getOrder().getOrderCode())
                .supplierName(deliveryRequest.getSupplierName())
                .materialName(deliveryRequest.getMaterialName())
                .requestedQuantity(deliveryRequest.getRequestedQuantity())
                .requestedAt(deliveryRequest.getRequestedAt())
                .status(deliveryRequest.getStatus() != null ? deliveryRequest.getStatus().name() : CurrentStatus.IN_PROGRESS.name())
                .build();
    }
}
