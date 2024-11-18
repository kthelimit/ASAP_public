package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ProductionPlanDTO;

public interface ProductionPlanService {
    Page<ProductionPlanDTO> getProductionPlans(Pageable pageable);
    void registerProductionPlan(ProductionPlanDTO productionPlanDTO);
    Page<ProductionPlanDTO> searchProductionPlans(String keyword, Pageable pageable);
}