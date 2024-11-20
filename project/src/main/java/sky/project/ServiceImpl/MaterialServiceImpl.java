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
import java.util.List;
import java.util.UUID;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private final String uploadDir = "C:/uploads/Images/";

    @Override
    public void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile) {
        Material material = new Material();
        material.setMaterialName(materialDTO.getMaterialName());
        material.setMaterialCode(materialDTO.getMaterialCode());
        material.setMaterialType(materialDTO.getMaterialType());
        material.setComponentType(materialDTO.getComponentType()); // 추가된 필드 설정
        material.setUnitPrice(materialDTO.getUnitPrice());
        material.setQuantity(materialDTO.getQuantity());
        material.setWidth(materialDTO.getWidth());
        material.setHeight(materialDTO.getHeight());
        material.setDepth(materialDTO.getDepth());
        material.setWeight(materialDTO.getWeight());
        material.setLeadtime(materialDTO.getLeadtime());

        // Supplier 조회 및 설정
        Supplier supplier = supplierRepository.findById(materialDTO.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        material.setSupplier(supplier);

        // 이미지 처리
        if (!imageFile.isEmpty()) {
            try {
                String imageUrl = saveImage(imageFile);  // 이미지 저장
                material.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                material.setImageUrl("/Images/default-image.jpg"); // 기본 이미지 설정
            }
        } else {
            material.setImageUrl("/Images/default-image.jpg"); // 이미지가 없을 경우 기본 이미지 설정
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
                .componentType(material.getComponentType()) // 추가된 필드 설정
                .unitPrice(material.getUnitPrice())
                .quantity(material.getQuantity())
                .width(material.getWidth())
                .height(material.getHeight())
                .depth(material.getDepth())
                .weight(material.getWeight()) // weight를 BigDecimal로 설정
                .imageUrl(material.getImageUrl())
                .supplierName(material.getSupplier().getSupplierName())
                .leadtime(material.getLeadtime())
                .build();
    }

    private String saveImage(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension; // 고유한 파일 이름 생성
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());  // 폴더가 없으면 생성
        file.transferTo(filePath.toFile());

        // 저장된 이미지 경로 반환 (URL에서 사용할 수 있는 형식으로 반환)
        return "/Images/" + fileName;
    }


    @Override
    public List<Material> getMaterialListWithComponentType(String ComponentType){
        List<Material> A= materialRepository.findByComponentType(ComponentType);
        A.forEach(material -> {
            System.out.println(material.getMaterialId());
            System.out.println(material.getMaterialName());
            System.out.println(material.getMaterialCode());
            System.out.println(material.getMaterialType());
        });
        return A;
    }
}