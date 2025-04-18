package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Entity.ProductionPlan;

import java.util.List;

public interface ProductionPlanService {
    Page<ProductionPlanDTO> getProductionPlans(Pageable pageable);

    List<ProductionPlan> getProductionPlans();

    Page<ProductionPlanDTO> searchProductionPlans(String keyword, Pageable pageable);

    String registerProductionPlan(ProductionPlanDTO productionPlanDTO);

    ProductionPlanDTO getProductionPlanById(Long id); // ID로 계획 조회

    String updateProductionPlan(ProductionPlanDTO productionPlanDTO); // 계획 수정

    String updateProductionPlanInProgress(String productionPlanCode);

    String updateProductionPlanFinshed(String productionPlanCode);

     void deleteProductionPlan(Long id);

    List<ProductionPlanDTO> getProductionPlansWithDate();

    List<ProductionPlan> getProductionPlanFinished(String productCode);


    //대시보드의 생산 계획 수
    int getCountProductionPlan();

    Page<ProductionPlanDTO> getProductionPlansByStatus(List<String> statuses, Pageable pageable);

}