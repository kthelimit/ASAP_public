package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.BomDTO;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.ProcurementPlan;
import sky.project.Service.BomService;
import sky.project.Service.ProcurementPlanService;
import sky.project.response.CustomErrorResponse;
import sky.project.response.SuccessResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/procure")
public class ProcurementPlanController {



    @Autowired
    private ProcurementPlanService procurementPlanService;

    @Autowired
    private BomService bomService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProcurement(@RequestBody List<ProcurementPlanDTO> procurementPlanDTOs) {
        System.out.println("prcurementPlanController register 메서드 실행.");

        try {
            for (ProcurementPlanDTO procurementPlanDTO : procurementPlanDTOs) {
                if (procurementPlanDTO.getProcurePlanCode() == null || procurementPlanDTO.getProcurePlanCode().isEmpty()) {
                    if (procurementPlanDTO.getProductName() == null || procurementPlanDTO.getProductName().isEmpty()) {
                        throw new IllegalArgumentException("Product name is required to generate a procurement plan code.");
                    }

                    String generatedCode = procurementPlanService.generateProcurementPlanCode(procurementPlanDTO);
                    procurementPlanDTO.setProcurePlanCode(generatedCode);
                }

                // ProcurementPlan 객체 생성 및 저장
                ProcurementPlan procurementPlan = new ProcurementPlan();
                procurementPlan.setSupplierName(procurementPlanDTO.getSupplierName());
                procurementPlan.setMaterialName(procurementPlanDTO.getMaterialName());
                procurementPlan.setProcurementQuantity(procurementPlanDTO.getProcurementQuantity());
                procurementPlan.setProcurementDueDate(procurementPlanDTO.getProcurementDueDate());
                procurementPlan.setProductName(procurementPlanDTO.getProductName());
                procurementPlan.setMaterialCode(procurementPlanDTO.getMaterialCode());
                procurementPlan.setProductCode(procurementPlanDTO.getProductCode());
                procurementPlan.setProductionPlanCode(procurementPlanDTO.getProductionPlanCode());
                procurementPlan.setProcurePlanCode(procurementPlanDTO.getProcurePlanCode());
                procurementPlan.setRequireQuantity(procurementPlanDTO.getRequireQuantity());
                procurementPlan.setStatus(CurrentStatus.ON_HOLD);

                procurementPlanService.save(procurementPlan);
            }

            // DTO의 isRegister 업데이트
            List<BomDTO> updatedBomList = bomService.findWithProductCode(procurementPlanDTOs.get(0).getProductCode());
            for (BomDTO bom : updatedBomList) {
                for (ProcurementPlanDTO procurementPlanDTO : procurementPlanDTOs) {
                    if (bom.getMaterialCode().equals(procurementPlanDTO.getMaterialCode())) {
                        bom.setRegister(true); // isRegister 값 업데이트
                        System.out.println("Updated BOM: " + bom.getMaterialName() + " isRegister=" + bom.isRegister());
                    }
                }
            }

            return ResponseEntity.ok(updatedBomList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomErrorResponse("조달 계획 등록 중 오류가 발생했습니다."));
        }
    }



//    // 조달 계획 조회 (목록)
//    @GetMapping("/list")
//    public ResponseEntity<?> getProcurementPlans() {
//        try {
//            List<ProcurementPlan> procurementPlans = procurementPlanService.findAll();
//            return ResponseEntity.ok(procurementPlans);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new CustomErrorResponse("조달 계획 목록 조회 중 오류가 발생했습니다."));
//        }
//    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getProcurementPlans(
            @RequestParam(defaultValue = "1") int page,   // 기본 페이지 번호는 1
            @RequestParam(defaultValue = "10") int size) { // 기본 페이지 크기는 10

        // 페이지 번호는 0부터 시작하므로 클라이언트에서 받은 페이지 번호에서 -1 해줍니다.
        Page<ProcurementPlanDTO> procurementPlansPage = procurementPlanService.getProcurementPlans(page, size);

        // 결과를 Map에 담아서 반환
        Map<String, Object> response = new HashMap<>();
        response.put("plans", procurementPlansPage.getContent()); // 실제 데이터
        response.put("currentPage", procurementPlansPage.getNumber() + 1); // 현재 페이지 번호 (0-based → 1-based로 변환)
        response.put("totalPages", procurementPlansPage.getTotalPages()); // 전체 페이지 수
        response.put("totalItems", procurementPlansPage.getTotalElements()); // 전체 데이터 수

        return ResponseEntity.ok(response);
    }


//    @PostMapping("/save")
//    public String saveProductionPlan(@ModelAttribute ProcurementPlanDTO procurementPlanDTO) {
//        if (procurementPlanDTO.getPlanId() == null) {
//            // 등록 동작
//            procurementPlanService.registerProcurementPlan(procurementPlanDTO);
//        } else {
//            // 수정 동작
//            procurementPlanService.updateProcurementPlan(procurementPlanDTO);
//        }
//        return ;
//    }

}
