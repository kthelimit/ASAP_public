package sky.project.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.ProcurementPlan;

import java.util.List;

public interface ProcurementPlanService {

//    void registerProcurementPlan(ProcurementPlanDTO procurementPlanDTO);
//
//    void updateProcurementPlan(ProcurementPlanDTO procurementPlanDTO);


    Page<ProcurementPlanDTO> getAllProcurementPlan(Pageable pageable);

    Page<ProcurementPlanDTO> searchProcurementPlansOnHold(String keyword, Pageable pageable);

    Page<ProcurementPlanDTO> searchProcurementPlans(String keyword, Pageable pageable);

    ProcurementPlan save(ProcurementPlan procurementPlan);

    Page<ProcurementPlanDTO> getProcurementPlans(Pageable pageable);

    int getCountProcurementPlanOnHold();

    boolean ProcurementCheckWithMaterialCodeAndProductionPlanCode(String materialCode, String productionPlanCode);

    List<ProcurementPlan> findPlanNotOrdered(String materialCode);

    int findSumProcuredQuantityPlanNotOrdered(String materialCode);

    String generateProcurementPlanCode(ProcurementPlanDTO dto);

//    List<ProcurementPlan> findAll();

    ProcurementPlanDTO getProcurementPlanById(Long id);

    Page<ProcurementPlanDTO> getProcurementPlans(int page, int size);
}
