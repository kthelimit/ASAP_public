package sky.project.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.DTO.ProductionPlanDTO;

import java.util.List;

public interface ProcurementPlanService {

    Page<ProcurementPlanDTO> getAllProcurementPlan(Pageable pageable);
    Page<ProcurementPlanDTO> searchProcurementPlans(String keyword, Pageable pageable);
}
