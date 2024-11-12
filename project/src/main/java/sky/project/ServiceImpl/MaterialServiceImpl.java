package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.Entity.Material;
import sky.project.Entity.Supplier;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.SupplierRepository;
import sky.project.Service.MaterialService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private final String uploadDir = "src/main/resources/static/Images/";

    @Override
    public void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile) {
        Material material = new Material();
        material.setMaterialName(materialDTO.getMaterialName());
        material.setMaterialCode(materialDTO.getMaterialCode());
        material.setMaterialType(materialDTO.getMaterialType());
        material.setUnit(materialDTO.getUnit());
        material.setUnitPrice(materialDTO.getUnitPrice());
        material.setQuantity(materialDTO.getQuantity());

        Supplier supplier = supplierRepository.findById(materialDTO.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        material.setSupplier(supplier);

        if (!imageFile.isEmpty()) {
            try {
                String imageUrl = saveImage(imageFile);  // 이미지 저장 로직
                material.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                // 기본 이미지 또는 오류 시 처리
                material.setImageUrl("/default-image.jpg"); // 기본 이미지 설정
            }
        } else {
            material.setImageUrl("/default-image.jpg"); // 이미지가 없을 경우 기본 이미지 설정
        }

        materialRepository.save(material);
    }

    @Override
    public Page<MaterialDTO> getMaterials(PageRequest pageRequest) {
        return materialRepository.findAll(pageRequest).map(this::ToDTO);
    }

    private MaterialDTO ToDTO(Material material) {
        return MaterialDTO.builder()
                .materialId(material.getMaterialId())
                .materialName(material.getMaterialName())
                .materialCode(material.getMaterialCode())
                .materialType(material.getMaterialType())
                .unit(material.getUnit())
                .unitPrice(material.getUnitPrice())
                .quantity(material.getQuantity())
                .imageUrl(material.getImageUrl())
                .supplierName(material.getSupplier().getSupplierName())
                .build();
    }

    private String saveImage(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension; // 고유한 파일 이름 생성
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath.toFile());
        return "/Images/" + fileName; // 저장된 이미지 경로 반환
    }

    public String getSupplierNameByUserId(String userId) {
        return supplierRepository.findByUser_UserId(userId)
                .map(Supplier::getSupplierName)
                .orElse(null);
    }
}
