package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ImportDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Import;
import sky.project.Entity.Material;
import sky.project.Entity.Stock;
import sky.project.Repository.ImportRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.StockRepository;
import sky.project.Service.ImportService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImportServiceImpl implements ImportService {



    @Autowired
    public ImportRepository importRepository;

    @Autowired
    public StockRepository stockRepository;

    @Autowired
    public MaterialRepository materialRepository;

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

        if (passedQuantity != null) {
            importEntity.setPassedQuantity(passedQuantity);

            // 결함 수량 계산 및 업데이트
            int defectiveQuantity = importEntity.getOrderedQuantity() - passedQuantity;
            if (defectiveQuantity < 0) {
                throw new RuntimeException("Defective quantity cannot be negative");
            }
            importEntity.setDefectiveQuantity(defectiveQuantity);

            // Stock 업데이트 로직
            Material material = materialRepository.findFirstByMaterialName(importEntity.getMaterialName())
                    .orElseThrow(() -> new RuntimeException("Material not found"));

            Stock stock = stockRepository.findByMaterialCode(material.getMaterialCode());
            if (stock == null) {
                throw new RuntimeException("Stock not found for the given material");
            }

            stock.setQuantity(stock.getQuantity() + passedQuantity);
            stockRepository.save(stock);
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


    // 엔티티 -> DTO 변환
    private ImportDTO toDTO(Import importEntity) {

        String materialCode = materialRepository.findFirstByMaterialName(importEntity.getMaterialName())
                .map(Material::getMaterialCode)
                .orElse("정보 없음");


        return ImportDTO.builder()
                .importId(importEntity.getImportId())
                .orderCode(importEntity.getOrderCode())
                .materialCode(materialCode)
                .materialName(importEntity.getMaterialName())
                .supplierName(importEntity.getSupplierName())
                .orderedQuantity(importEntity.getOrderedQuantity())
                .quantity(importEntity.getQuantity())
                .passedQuantity(importEntity.getPassedQuantity())
                .importStatus(importEntity.getImportStatus())
                .build();
    }

    // DTO -> 엔티티 변환
    private Import toEntity(ImportDTO importDTO) {
        Import importEntity = new Import();
        importEntity.setOrderCode(importDTO.getOrderCode());
        importEntity.setMaterialName(importDTO.getMaterialName()); // materialName 직접 설정
        importEntity.setSupplierName(importDTO.getSupplierName());
        importEntity.setOrderedQuantity(importDTO.getOrderedQuantity());
        importEntity.setQuantity(importDTO.getQuantity());
        importEntity.setPassedQuantity(importDTO.getPassedQuantity());
        importEntity.setImportStatus(importDTO.getImportStatus());
        return importEntity;
    }
}