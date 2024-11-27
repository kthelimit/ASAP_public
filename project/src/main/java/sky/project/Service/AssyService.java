package sky.project.Service;

import sky.project.DTO.AssyDTO;

import java.util.List;

public interface AssyService {

    //등록
    Long register(AssyDTO dto);

    List<AssyDTO> findByAssyMaterialCode(String assyMaterialCode);

    int findLeftQuantityByAssyMaterialCode(String productionPlanCode, String assyMaterialCode);
}
