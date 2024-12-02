package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.ImportDTO;
import sky.project.Entity.Import;
import sky.project.Entity.Material;
import sky.project.Repository.ImportRepository;
import sky.project.Repository.MaterialRepository;
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

    @Autowired
    public MaterialRepository materialRepository;

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