package sky.project.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.ReturnDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.DeliveryRequestService;
import sky.project.Service.ReturnService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ReturnServiceImpl implements ReturnService {

    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private DeliveryRequestService deliveryRequestService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public Long register(ReturnDTO dto) {

        Returns entity = dtoToEntity(dto);

        //반품코드 생성
        entity.setReturnCode(generateReturnCode(dto));

        returnRepository.save(entity);
        return entity.getReturnId();
    }

    @Override
    public void returnUpdate(Long returnId) {
        Returns entity = returnRepository.findById(returnId).orElse(null);
        if (entity != null) {
            //불량품 재배송 완료
            entity.setStatus(CurrentStatus.FINISHED);

            //재배송된 수량을 다시 창고에 넣는다.
            // Stock 업데이트 로직
            Material material = materialRepository.findFirstByMaterialName(entity.getMyImport().getMaterialName())
                    .orElseThrow(() -> new RuntimeException("Material not found"));

            Stock stock = stockRepository.findByMaterialCode(material.getMaterialCode());
            if (stock == null) {
                throw new RuntimeException("Stock not found for the given material");
            }

            stock.setQuantity(stock.getQuantity() + entity.getQuantity());
            stockRepository.save(stock);

            //관련 납품지시 완료처리
            deliveryRequestService.updateRequestStatus(entity.getMyImport().getDeliveryRequest().getId(), "FINISHED");

            String orderCode = entity.getMyImport().getOrderCode();
            Order order = orderRepository.findByOrderCode(orderCode).orElse(null);

            //만약 발주서가 전부 배송된 상태라면
            if (order != null && order.getStatus() == CurrentStatus.DELIVERED) {
                //납품지시들이 전부 완료된 경우 발주 완료 처리
                List<DeliveryRequest> deliveryRequests = deliveryRequestService.findByOrderCode(orderCode);
                List<DeliveryRequest> finishedRequests = deliveryRequestService.findByFinishedRequests(orderCode);
                log.info("총 납품지시 건수 : "+deliveryRequests.size());
                log.info("완료된 납품지시 건수 : "+finishedRequests.size());
                if (deliveryRequests.size() == finishedRequests.size()) {
                    order.setStatus(CurrentStatus.FINISHED);
                    orderRepository.save(order);
                }
            }
        }

    }


    @Override
    public ReturnDTO findByImportId(Long importId) {
        return entityToDto(returnRepository.findByImportId(importId));
    }

    @Override
    public List<ReturnDTO> findBySupplierNameOnHOLD(String supplierName) {
        return returnRepository.findBySupplierNameOnHOLD(supplierName).stream().map(this::entityToDto).toList();
    }

    @Override
    public int getCountReturnNotFinished(String supplierName){
        return returnRepository.countBySupplierNameOnHOLD(supplierName);
    }

    private Returns dtoToEntity(ReturnDTO dto) {

        Import imp = importRepository.findById(dto.getImportId()).orElse(null);

        return Returns.builder()
                .returnId(dto.getReturnId())
                .returnCode(dto.getReturnCode())
                .myImport(imp)
                .quantity(dto.getQuantity())
                .status(CurrentStatus.ON_HOLD)
                .build();
    }

    private ReturnDTO entityToDto(Returns entity) {
        ReturnDTO dto = ReturnDTO.builder()
                .returnId(entity.getReturnId())
                .returnCode(entity.getReturnCode())
                .importId(entity.getMyImport().getImportId())
                .importCode(entity.getMyImport().getImportCode())
                .quantity(entity.getQuantity())
                .createdDate(entity.getCreatedDate())
                .materialName(entity.getMyImport().getMaterialName())
                .status(entity.getStatus().name())
                .build();
        return dto;
    }


    private String generateReturnCode(ReturnDTO dto) {
        // 매터리얼 코드에 따른 접두어 설정
        String prefix = "RET";
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
        Long nextSequence = returnRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}
