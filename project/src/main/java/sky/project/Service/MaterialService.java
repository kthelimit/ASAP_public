package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;

import java.util.List;

public interface MaterialService {
    void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile);

    //부품 타입으로 자재 리스트 불러오기
    List<MaterialDTO> findBycomponentType(String componentType);

    //자재 타입으로 자재 리스트 불러오기
    List<MaterialDTO> findByMaterialType(String materialType);

    Page<MaterialDTO> getMaterials(PageRequest pageRequest);

}