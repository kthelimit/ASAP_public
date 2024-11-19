package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.Entity.Material;

import java.util.List;

public interface MaterialService {
    void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile);

    List<Material> getMaterialListWithComponentType(String ComponentType);

    Page<MaterialDTO> getMaterials(PageRequest pageRequest);

}