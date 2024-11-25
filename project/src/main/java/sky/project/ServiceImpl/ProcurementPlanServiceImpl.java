package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.ProcurementPlan;
import sky.project.Repository.ProcurementPlanRepository;
import sky.project.Service.ProcurementPlanService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProcurementPlanServiceImpl implements ProcurementPlanService {

    @Autowired
    ProcurementPlanRepository procurementPlanRepository;

    @Override
    public Page<ProcurementPlanDTO> getAllProcurementPlan(Pageable pageable) {
        return procurementPlanRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public List<ProcurementPlan> findAll() {
        return procurementPlanRepository.findAll();
    }

    @Override
    public ProcurementPlanDTO getProcurementPlanById(Long id) {
        ProcurementPlan plan = procurementPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + id));
        return toDTO(plan);
    }

    @Override
    public Page<ProcurementPlanDTO> searchProcurementPlans(String keyword, Pageable pageable) {
        // 키워드로 supplierName 또는 materialName 검색
        Page<ProcurementPlan> procurementPlansPage =
                procurementPlanRepository.findBySupplierNameContainingOrMaterialNameContaining(keyword, keyword, pageable);
        return procurementPlansPage.map(this::toDTO);
    }

    public ProcurementPlan save(ProcurementPlan procurementPlan) {
        return procurementPlanRepository.save(procurementPlan);
    }

//    @Override
//    public void registerProcurementPlan(ProcurementPlanDTO procurementPlanDTO) {
//        ProcurementPlan plan = toEntity(procurementPlanDTO);
//
//        // 생산 계획 코드 생성 로직 추가
//        String productionPlanCode = generateProcurementPlanCode(procurementPlanDTO);
//        plan.setProductionPlanCode(productionPlanCode);
//
//        procurementPlanRepository.save(plan);
//    }

//    @Override
//    public void updateProcurementPlan(ProcurementPlanDTO procurementPlanDTO) {
//        ProcurementPlan plan = procurementPlanRepository.findById(procurementPlanDTO.getPlanId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 조달 계획을 찾을 수 없습니다: " + procurementPlanDTO.getPlanId()));
//
//        // 기존 조달 계획 코드는 수정하지 않음
//        plan.setProcurementDueDate(procurementPlanDTO.getProcurementDueDate());
//        plan.setProductionPlanCode(procurementPlanDTO.getProductionPlanCode());
//        plan.setProductCode(procurementPlanDTO.getProductCode());
//        plan.setProductName(procurementPlanDTO.getProductName());
//        plan.setSupplierName(procurementPlanDTO.getSupplierName());
//        plan.setMaterialName(procurementPlanDTO.getMaterialName());
//        plan.setProcurementQuantity(procurementPlanDTO.getProcurementQuantity());
//        plan.setProcurementDueDate(procurementPlanDTO.getProcurementDueDate());
//        plan.setProductName(procurementPlanDTO.getProductName());
//        plan.setMaterialCode(procurementPlanDTO.getMaterialCode());
//        plan.setProductionPlanCode(procurementPlanDTO.getProductionPlanCode());
//
//        procurementPlanRepository.save(plan);
//    }

    public String generateProcurementPlanCode(ProcurementPlanDTO dto) {
        // 제품명에 따른 접두어 설정
        String prefix;
        switch (dto.getProductName()) {
            case "전기자전거A":
                prefix = "PCMPBA";
                break;
            case "전기자전거B":
                prefix = "PCMPBB";
                break;
            case "전동킥보드":
                prefix = "PCMPBK";
                break;
            default:
                prefix = "PCMPUN";
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        String dateCode = dto.getProcurementDueDate().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = procurementPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
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

    // DTO to Entity 변환
    private ProcurementPlan toEntity(ProcurementPlanDTO dto) {
        if (dto == null) return null;

        ProcurementPlan plan = new ProcurementPlan();
        plan.setPlanId(dto.getPlanId());
        plan.setProductionPlanCode(dto.getProductionPlanCode());
        plan.setProcurePlanCode(dto.getProcurePlanCode());
        plan.setProductCode(dto.getProductCode());
        plan.setProductName(dto.getProductName());
        plan.setSupplierName(dto.getSupplierName());
        plan.setMaterialName(dto.getMaterialName());
        plan.setMaterialCode(dto.getMaterialCode());
        plan.setProcurementQuantity(dto.getProcurementQuantity());
        plan.setProcurementDueDate(dto.getProcurementDueDate());
        return plan;
    }
}
