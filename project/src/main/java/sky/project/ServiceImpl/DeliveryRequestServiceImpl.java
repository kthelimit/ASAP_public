package sky.project.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.DeliveryRequestDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.DeliveryRequestService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Slf4j
@Service
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private SupplierStockRepository supplierStockRepository;

    @Override
    public Long createRequest(DeliveryRequestDTO dto) {

        // DeliveryRequest 생성 및 저장
        DeliveryRequest deliveryRequest = toEntity(dto);

        //납품 코드
        deliveryRequest.setDeliveryCode(generateDeliveryCode(dto));

        deliveryRequestRepository.save(deliveryRequest);

        return deliveryRequest.getId();
    }

    @Override
    public DeliveryRequest findById(Long id) {
        return deliveryRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DeliveryRequest not found with ID: " + id));
    }

    @Override
    public List<DeliveryRequest> findByOrderCode(String orderCode){
        return deliveryRequestRepository.findDeliveryRequestsByOrderCode(orderCode);
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

    //대시보드 출력용 이번달 납품 지시 건수
    @Override
    public int getCountRequestThisMonth() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return deliveryRequestRepository.countDeliveryRequestsThisMonth(start, end);
    }

    //대시보드 출력용 이번달 납품 지시 건수 업체용
    @Override
    public int getCountRequestThisMonth(String supplierName) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return deliveryRequestRepository.countDeliveryRequestsThisMonth(start, end, supplierName);
    }

    @Override
    public int getCountRequestNotYet(String supplierName){
        return deliveryRequestRepository.countDeliveryRequestsInProgress(supplierName);
    }


    @Override
    public List<DeliveryRequest> findByDeliveredRequests(String orderCode){
        return deliveryRequestRepository.findDeliveryRequestsByOrderCodeDelivered(orderCode);
    }


    @Override
    public List<DeliveryRequest> findByFinishedRequests(String orderCode){
        return deliveryRequestRepository.findDeliveryRequestsByOrderCodeFinished(orderCode);
    }

    private DeliveryRequest toEntity(DeliveryRequestDTO dto) {
        // 주문 엔티티 조회
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Supplier supplier = order.getSupplier();
        Material material = order.getMaterial();

        // DeliveryRequest 생성
        return DeliveryRequest.builder()
                .order(order)
                .deliveryCode(dto.getDeliveryCode())
                .supplier(supplier)
                .material(material)
                .requestedQuantity(dto.getRequestedQuantity())
                .requestedDate(dto.getRequestedDate())
                .status(CurrentStatus.IN_PROGRESS) // 기본 상태 설정
                .build();
    }


    private DeliveryRequestDTO toDTO(DeliveryRequest entity) {

        //업체의 가용 재고를 불러온다.
        SupplierStock stock = supplierStockRepository.findByMaterialCodeWithSupplierId(entity.getSupplier().getSupplierId(), entity.getMaterial().getMaterialCode());
        int availableStock = stock.getStock();

        //지금 이 납입지시 이전 날짜의 납입지시 중 활성화되어 있는 것들의 수량을 제외한다.
        List<DeliveryRequest> list = deliveryRequestRepository.findDeliveryRequestsByRequestedDateAndStatus(entity.getRequestedDate());
        int sum = 0;
        for (DeliveryRequest deliveryRequest : list) {
            sum += deliveryRequest.getRequestedQuantity();
        }
        availableStock -= sum;

        //이 납입 지시 이전의 납입지시 중 같은 주문서인 것들의 수량을 계산한다.
        int remainingQuantity = entity.getOrder().getOrderQuantity();
        LocalDateTime createdDate = entity.getCreatedDate();
        List<DeliveryRequest> deliveryRequestsInSameOrder = deliveryRequestRepository.findDeliveryRequestsByOrderCodeBefore(entity.getOrder().getOrderCode(), createdDate);
        int sum2 = 0;
        for (DeliveryRequest deliveryRequest : deliveryRequestsInSameOrder) {
            sum2 += deliveryRequest.getRequestedQuantity();
        }
        remainingQuantity -= sum2;
        int remainingQuantityAfter = remainingQuantity - entity.getRequestedQuantity();

        return DeliveryRequestDTO.builder()
                .id(entity.getId())
                .deliveryCode(entity.getDeliveryCode())
                .orderId(entity.getOrder().getOrderId())
                .orderCode(entity.getOrder().getOrderCode())
                .supplierName(entity.getSupplier().getSupplierName())
                .materialName(entity.getMaterial().getMaterialName())
                .requestedQuantity(entity.getRequestedQuantity())
                .requestedDate(entity.getRequestedDate())
                .status(entity.getStatus() != null ? entity.getStatus().name() : CurrentStatus.IN_PROGRESS.name())
                .availableStock(availableStock)
                .remainingQuantity(remainingQuantity)
                .remainingQuantityAfter(remainingQuantityAfter)
                .totalOrderQuantity(entity.getOrder().getOrderQuantity())
                .build();
    }

    private String generateDeliveryCode(DeliveryRequestDTO dto) {
        // 매터리얼 코드에 따른 접두어 설정
        String prefix = "DEL";
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
        Long nextSequence = deliveryRequestRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}
