package sky.project.Service;

import sky.project.DTO.ReturnDTO;

public interface ReturnService {
    Long register(ReturnDTO dto);

    ReturnDTO findByImportId(Long importId);
}
