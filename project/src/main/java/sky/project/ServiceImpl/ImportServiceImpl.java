package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.ImportDTO;
import sky.project.Entity.Import;
import sky.project.Entity.Material;
import sky.project.Repository.ImportRepository;
import sky.project.Service.ImportService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImportServiceImpl implements ImportService {

    private final ImportRepository importRepository;

    @Autowired
    public ImportServiceImpl(ImportRepository importRepository) {
        this.importRepository = importRepository;
    }

    @Override
    public List<ImportDTO> getAllImports() {
        List<Import> imports = importRepository.findAll();
        return imports.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ImportDTO createImport(ImportDTO importDTO) {
        Import importEntity = toEntity(importDTO);
        importEntity = importRepository.save(importEntity);
        return toDTO(importEntity);
    }

    @Override
    public ImportDTO updateImport(Long importId, ImportDTO importDTO) {
        Optional<Import> existingImport = importRepository.findById(importId);
        if (existingImport.isPresent()) {
            Import importEntity = existingImport.get();
            importEntity.setOrderId(importDTO.getOrderId());
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

    // 엔티티 -> DTO 변환
    private ImportDTO toDTO(Import importEntity) {
        return ImportDTO.builder()
                .importId(importEntity.getImportId())
                .orderId(importEntity.getOrderId())
                .materialCode(importEntity.getMaterial().getMaterialCode())
                .materialName(importEntity.getMaterial().getMaterialName())
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
        Material material = new Material();
        material.setMaterialCode(importDTO.getMaterialCode()); // 자재 코드 설정 (실제 자재 조회 필요)

        importEntity.setOrderId(importDTO.getOrderId());
        importEntity.setMaterial(material); // 실제 자재 엔티티 설정
        importEntity.setSupplierName(importDTO.getSupplierName());
        importEntity.setOrderedQuantity(importDTO.getOrderedQuantity());
        importEntity.setQuantity(importDTO.getQuantity());
        importEntity.setPassedQuantity(importDTO.getPassedQuantity());
        importEntity.setImportStatus(importDTO.getImportStatus());
        return importEntity;
    }
}