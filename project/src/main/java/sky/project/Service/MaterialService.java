package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.Entity.Material;

import java.util.List;

public interface MaterialService {
    void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile);

    //부품 타입으로 자재 리스트 불러오기
    List<MaterialDTO> findByComponentType(String componentType);
    
    //자재 타입으로 자재 리스트 불러오기
    List<MaterialDTO> findByMaterialType(String materialType);
    Page<MaterialDTO> getMaterials(Pageable pageable); // 기존 메서드
    Page<MaterialDTO> searchMaterials(String keyword, Pageable pageable);

    List<MaterialDTO> findAssyMaterialByProductCode(String productCode);
    List<MaterialDTO> getSuppliersByMaterialName(String materialName);
    int getAvailableStock(String materialCode);

    Material getMaterialByName(String materialName);
}