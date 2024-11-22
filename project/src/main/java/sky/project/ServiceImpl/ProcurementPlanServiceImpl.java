package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.ProcurementPlan;
import sky.project.Repository.ProcurementPlanRepository;
import sky.project.Service.ProcurementPlanService;

@Service
public class ProcurementPlanServiceImpl implements ProcurementPlanService {

    @Autowired
    ProcurementPlanRepository procurementPlanRepository;

    @Override
    public Page<ProcurementPlanDTO> getAllProcurementPlan(Pageable pageable) {
        return procurementPlanRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<ProcurementPlanDTO> searchProcurementPlans(String keyword, Pageable pageable) {
        // 키워드로 supplierName 또는 materialName 검색
        Page<ProcurementPlan> procurementPlansPage =
                procurementPlanRepository.findBySupplierNameContainingOrMaterialNameContaining(keyword, keyword, pageable);
        return procurementPlansPage.map(this::toDTO);
    }

    // Entity to DTO 변환
    private ProcurementPlanDTO toDTO(ProcurementPlan plan) {
        if (plan == null) return null;

        ProcurementPlanDTO dto = new ProcurementPlanDTO();
        dto.setPlanId(plan.getPlanId());
        dto.setProductionPlanCode(plan.getProductionPlanCode());
        dto.setProcurePlanCode(plan.getProcurePlanCode());
        dto.setProductCode(plan.getProductCode());
        dto.setProductName(plan.getProductName());
        dto.setSupplierName(plan.getSupplierName());
        dto.setMaterialName(plan.getMaterialName());
        dto.setMaterialCode(plan.getMaterialCode());
        dto.setProcurementQuantity(plan.getProcurementQuantity());
        dto.setProcurementDueDate(plan.getProcurementDueDate());
        return dto;
    }
}
