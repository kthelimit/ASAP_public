package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;

public interface MaterialService {
    void registerMaterial(MaterialDTO materialDTO, MultipartFile imageFile);
    Page<MaterialDTO> getMaterials(PageRequest pageRequest);
}