package sky.project.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ImportDTO;
import sky.project.DTO.ReturnDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.DeliveryRequestService;
import sky.project.Service.ImportService;
import sky.project.Service.ReturnService;
import sky.project.Service.StockTrailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImportServiceImpl implements ImportService {


    @Autowired
    public ImportRepository importRepository;

    @Autowired
    public StockRepository stockRepository;

    @Autowired
    public MaterialRepository materialRepository;
    @Autowired
    public ReturnService returnService;
    @Autowired
    private DeliveryRequestService deliveryRequestService;
    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockTrailService stockTrailService;


    @Override
    public Page<ImportDTO> getImportsByCriteria(String type, String keyword, Pageable pageable) {
        Page<Import> imports;

        // 상태별 데이터를 가져오는 것이 아니라, 모든 데이터를 가져옴
        if ("material".equalsIgnoreCase(type)) {
            imports = importRepository.findByMaterialNameContaining(keyword, pageable);
        } else if ("supplier".equalsIgnoreCase(type)) {
            imports = importRepository.findBySupplierNameContaining(keyword, pageable);
        } else if ("order".equalsIgnoreCase(type)) {
            imports = importRepository.findByOrderCodeContaining(keyword, pageable);
        } else {
            imports = importRepository.findAll(pageable); // 검색 조건이 없으면 전체 조회
        }

        // Import -> ImportDTO 변환
        return imports.map(this::toDTO);
    }

    @Override
    public void updateImportStatus(Long importId,
                                   CurrentStatus status,
                                   Integer passedQuantity) {
        // Import 엔티티 조회
        Import importEntity = importRepository.findById(importId)
                .orElseThrow(() -> new RuntimeException("Import not found"));

        importEntity.setImportStatus(status);
        //검수를 시작하면 납품지시 쪽을 도착으로 바꿔준다.
        if(status==CurrentStatus.UNDER_INSPECTION){
            deliveryRequestService.updateRequestStatus(importEntity.getDeliveryRequest().getId(), "ARRIVED");
        }

        if (passedQuantity != null) {
            importEntity.setPassedQuantity(passedQuantity);

            // 결함 수량 계산 및 업데이트
            int defectiveQuantity = importEntity.getOrderedQuantity() - passedQuantity;

            // 파본 대비로 더 넣었다거나 해서 실제보다 더 많은 수량이 합격하면 오류나게 되므로 일단 주석 처리함.
//            if (defectiveQuantity < 0) {
//                throw new RuntimeException("Defective quantity cannot be negative");
//            }

            importEntity.setDefectiveQuantity(defectiveQuantity);
            log.info("defectiveQuantity : " + defectiveQuantity);
            //결함수량이 0보다 크면 반품 테이블에 입력함
            if (defectiveQuantity > 0) {
                ReturnDTO returnDTO = ReturnDTO.builder()
                        .materialName(importEntity.getMaterialName())
                        .quantity(defectiveQuantity)
                        .importId(importId)
                        .build();
                returnService.register(returnDTO);
                //반품 수량이 있으니 반품중으로 표시
            }else{
                //결함수량이 0보다 크지 않다면 무사히 완료된 것이므로 납품지시를 완료로 바꿈
                deliveryRequestService.updateRequestStatus(importEntity.getDeliveryRequest().getId(), "FINISHED");
                String orderCode = importEntity.getOrderCode();
                Order order = orderRepository.findByOrderCode(orderCode).orElse(null);
                //만약 발주서가 전부 배송된 상태라면
                if (order != null && order.getStatus() == CurrentStatus.DELIVERED) {
                    //납품지시들이 전부 완료된 경우 발주 완료 처리
                    List<DeliveryRequest> deliveryRequests = deliveryRequestService.findByOrderCode(orderCode);
                    List<DeliveryRequest> finishedRequests = deliveryRequestService.findByFinishedRequests(orderCode);
                    if (deliveryRequests.size() == finishedRequests.size()) {
                        order.setStatus(CurrentStatus.FINISHED);
                        orderRepository.save(order);
                    }
                }

            }

            // Stock 업데이트 로직
            Material material = materialRepository.findFirstByMaterialName(importEntity.getMaterialName())
                    .orElseThrow(() -> new RuntimeException("Material not found"));

            Stock stock = stockRepository.findByMaterialCode(material.getMaterialCode());
            if (stock == null) {
                throw new RuntimeException("Stock not found for the given material");
            }

            stock.setQuantity(stock.getQuantity() + passedQuantity);
            stockRepository.save(stock);

            //추적용
            StockTrail stockTrail = StockTrail.builder()
                    .material(material)
                    .quantity(passedQuantity)
                    .stock(stock.getQuantity())
                    .date(LocalDateTime.now())
                    .build();
            stockTrailService.register(stockTrail);

        }

        importRepository.save(importEntity);
    }

    @Override
    public List<ImportDTO> getAllImports() {
        List<Import> imports = importRepository.findAll();
        return imports.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ImportDTO createImport(ImportDTO importDTO) {
        // DTO를 Import 엔티티로 변환
        Import importEntity = toEntity(importDTO);

        //입고 코드 생성
        importEntity.setImportCode(generateOrderCode(importDTO));

        // Import 엔티티 저장
        Import savedImportEntity = importRepository.save(importEntity);

        // 저장된 엔티티를 ImportDTO로 변환하여 반환
        return toDTO(savedImportEntity);
    }

    @Override
    public ImportDTO updateImport(Long importId, ImportDTO importDTO) {
        Optional<Import> existingImport = importRepository.findById(importId);
        if (existingImport.isPresent()) {
            Import importEntity = existingImport.get();
            importEntity.setOrderCode(importDTO.getOrderCode());
            importEntity.setSupplierName(importDTO.getSupplierName());
            importEntity.setOrderedQuantity(importDTO.getOrderedQuantity());
            importEntity.setQuantity(importDTO.getQuantity());
            importEntity.setPassedQuantity(importDTO.getPassedQuantity());
            importEntity.setImportStatus(importDTO.getImportStatus());
            importEntity = importRepository.save(importEntity);
            return toDTO(importEntity);
        } else {
            throw new RuntimeException("Import not found with ID " + importId);
        }
    }

    @Override
    public void deleteImport(Long importId) {
        if (importRepository.existsById(importId)) {
            importRepository.deleteById(importId);
        } else {
            throw new RuntimeException("Import not found with ID " + importId);
        }
    }

    @Override
    public ImportDTO getImportById(Long importId) {
        Optional<Import> importEntity = importRepository.findById(importId);
        if (importEntity.isPresent()) {
            return toDTO(importEntity.get());
        } else {
            throw new RuntimeException("Import not found with ID " + importId);
        }
    }

    @Override
    public List<ImportDTO> calculateDefectiveQuantity(List<ImportDTO> importList) {
        for (ImportDTO importDTO : importList) {
            // 결함 수량 계산 (예시: 입고 수량에서 합격 수량을 빼는 방식)
            int defectiveQuantity = importDTO.getOrderedQuantity() - importDTO.getPassedQuantity();
            importDTO.setDefectiveQuantity(defectiveQuantity);
        }
        return importList;
    }

    @Override
    public List<ImportDTO> getRecentImportList() {
        return importRepository.findRecentImport().stream().map(this::toDTO).toList();
    }

    @Override
    public int getCountImportOnHold() {
        return importRepository.countImportOnHold();
    }

    @Override
    public int getCountImportInInspection() {
        return importRepository.countImportUnderInspection();
    }

    // 엔티티 -> DTO 변환
    private ImportDTO toDTO(Import importEntity) {

        String materialCode = materialRepository.findFirstByMaterialName(importEntity.getMaterialName())
                .map(Material::getMaterialCode)
                .orElse("정보 없음");

        Material material = materialRepository.findByMaterialCode(materialCode).orElse(null);

        Order order = orderRepository.findByOrderCode(importEntity.getOrderCode()).orElse(null);

        ReturnDTO returnDTO = null;
        if (importEntity.getDefectiveQuantity() > 0) {
            returnDTO = returnService.findByImportId(importEntity.getImportId());
        }

        return ImportDTO.builder()
                .importId(importEntity.getImportId())
                .orderCode(importEntity.getOrderCode())
                .importCode(importEntity.getImportCode())
                .deliveryId(importEntity.getDeliveryRequest().getId())
                .defectiveQuantity(importEntity.getDefectiveQuantity())
                .materialCode(materialCode)
                .materialName(importEntity.getMaterialName())
                .materialType(material == null ? "" : material.getMaterialType())
                .supplierName(importEntity.getSupplierName())
                .orderedQuantity(importEntity.getOrderedQuantity())
                .orderDate(order == null ? null : order.getOrderDate())
                .quantity(importEntity.getQuantity())
                .passedQuantity(importEntity.getPassedQuantity())
                .importStatus(importEntity.getImportStatus())
                .returnDTO(returnDTO)
                .modifiedDate(importEntity.getModifiedDate())
                .build();
    }

    // DTO -> 엔티티 변환
    private Import toEntity(ImportDTO importDTO) {

        Import importEntity = new Import();

        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(importDTO.getDeliveryId()).orElse(null);

        importEntity.setOrderCode(importDTO.getOrderCode());
        importEntity.setMaterialName(importDTO.getMaterialName()); // materialName 직접 설정
        importEntity.setSupplierName(importDTO.getSupplierName());
        importEntity.setOrderedQuantity(importDTO.getOrderedQuantity());
        importEntity.setQuantity(importDTO.getQuantity());
        importEntity.setPassedQuantity(importDTO.getPassedQuantity());
        importEntity.setImportStatus(importDTO.getImportStatus());
        importEntity.setDeliveryRequest(deliveryRequest);
        return importEntity;
    }

    private String generateOrderCode(ImportDTO dto) {
        // 매터리얼 코드에 따른 접두어 설정
        String prefix = "IMP";
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
        Long nextSequence = importRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}