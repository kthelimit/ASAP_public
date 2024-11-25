package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ProductionPlanDTO;

import java.util.List;

public interface ProductionPlanService {
    Page<ProductionPlanDTO> getProductionPlans(Pageable pageable);

    void registerProductionPlan(ProductionPlanDTO productionPlanDTO);

    Page<ProductionPlanDTO> searchProductionPlans(String keyword, Pageable pageable);

    ProductionPlanDTO getProductionPlanById(Long id); // ID로 계획 조회

    void updateProductionPlan(ProductionPlanDTO productionPlanDTO); // 계획 수정

    void deleteProductionPlan(Long id);

    List<ProductionPlanDTO> getProductionPlansWithDate();
}