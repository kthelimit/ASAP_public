package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.Entity.Material;
import sky.project.Entity.Stock;
import sky.project.Entity.Supplier;
import sky.project.Repository.AssyRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.StockRepository;
import sky.project.Repository.SupplierRepository;
import sky.project.Service.MaterialService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    AssyRepository assyRepository;

    private final String uploadDir = "C:/uploads/Images/";

    @Override
    public Page<MaterialDTO> getMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<MaterialDTO> searchMaterials(String keyword, Pageable pageable) {
        return materialRepository
                .findByMaterialNameContainingOrMaterialCodeContaining(keyword, keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    public void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile) {
        Material material = new Material();
        material.setMaterialName(materialDTO.getMaterialName());
        material.setMaterialCode(materialDTO.getMaterialCode());
        material.setMaterialType(materialDTO.getMaterialType());
        material.setComponentType(materialDTO.getComponentType());
        material.setUnitPrice(materialDTO.getUnitPrice());
        material.setQuantity(materialDTO.getQuantity());
        material.setWidth(materialDTO.getWidth());
        material.setHeight(materialDTO.getHeight());
        material.setDepth(materialDTO.getDepth());
        material.setWeight(materialDTO.getWeight());
        material.setLeadtime(materialDTO.getLeadtime());

        // Supplier 설정
        Supplier supplier = supplierRepository.findById(materialDTO.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        material.setSupplier(supplier);

        // 이미지 처리
        material.setImageUrl(handleImageUpload(imageFile));

        materialRepository.save(material);
    }

    @Override
    public List<MaterialDTO> findByComponentType(String componentType) {
        return materialRepository.findByComponentType(componentType).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private MaterialDTO toDTO(Material material) {
        return MaterialDTO.builder()
                .materialId(material.getMaterialId())
                .materialName(material.getMaterialName())
                .materialCode(material.getMaterialCode())
                .materialType(material.getMaterialType())
                .componentType(material.getComponentType())
                .unitPrice(material.getUnitPrice())
                .quantity(material.getQuantity())
                .width(material.getWidth())
                .height(material.getHeight())
                .depth(material.getDepth())
                .weight(material.getWeight())
                .imageUrl(material.getImageUrl())
                .supplierName(material.getSupplier().getSupplierName())
                .leadtime(material.getLeadtime())
                .build();
    }

    private String handleImageUpload(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return "/Images/default-image.jpg"; // 기본 이미지
        }
        try {
            String fileName = generateUniqueFileName(imageFile.getOriginalFilename());
            Path filePath = Paths.get(uploadDir + fileName);

            Files.createDirectories(filePath.getParent());  // 폴더 생성
            imageFile.transferTo(filePath.toFile());

            return "/Images/" + fileName; // 이미지 경로 반환
        } catch (IOException e) {
            e.printStackTrace();
            return "/Images/default-image.jpg"; // 예외 발생 시 기본 이미지
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + fileExtension; // 고유 파일명 생성
    }


    @Override
    public List<MaterialDTO> getSuppliersByMaterialName(String materialName) {
        // 자재 이름에 매칭되는 자재 조회
        List<Material> materials = materialRepository.findByMaterialName(materialName);

        // DTO로 변환
        return materials.stream()
                .map(material -> MaterialDTO.builder()
                        .materialName(material.getMaterialName())
                        .supplierName(material.getSupplier().getSupplierName())
                        .unitPrice(material.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public int getAvailableStock(String materialCode) {
        // 자재 코드에 매칭되는 재고 정보 조회
        Stock stock = stockRepository.findByMaterialCode(materialCode);
        return stock != null ? stock.getAvailableStock() : 0;
    }




    @Override
   public List<MaterialDTO>  findByMaterialType(String materialType){
       List<Material> materialList =materialRepository.findByMaterialType(materialType);
       List<MaterialDTO> materialDTOList = new ArrayList<>();

       materialList.forEach(material -> {
           materialDTOList.add(toDTO(material));
       });
       return materialDTOList;
   }

    @Override
    public List<MaterialDTO> findAssyMaterialByProductCode(String productCode) {
        List<Material> materialList = assyRepository.findAssyMaterialByProductCode(productCode);
        List<MaterialDTO> materialDTOList = new ArrayList<>();
        materialList.forEach(material -> {
            materialDTOList.add(toDTO(material));
        });
        return materialDTOList;
    }


}