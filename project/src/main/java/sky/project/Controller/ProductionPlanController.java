package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.*;
import sky.project.Entity.ProductionPerDay;
import sky.project.Repository.ProductionPerDayRepository;
import sky.project.Service.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/plan")
public class ProductionPlanController {


    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private BomService bomService;

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ProcurementPlanService procurementPlanService;
    @Autowired
    private ProductionPerDayService productionPerDayService;
    @Autowired
    private ProductionPerDayRepository productionPerDayRepository;


    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        //최근에 등록한 것이 위에 뜨도록
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("planId").descending());
        Page<ProductionPlanDTO> plans;

        if (keyword != null && !keyword.isEmpty()) {
            plans = productionPlanService.searchProductionPlans(keyword, pageable);
        } else {
            plans = productionPlanService.getProductionPlans(pageable);
        }

        model.addAttribute("plans", plans.getContent());
        model.addAttribute("totalPages", plans.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        // 등록 및 수정에 사용할 빈 DTO 객체 추가
        model.addAttribute("productionPlanDTO", new ProductionPlanDTO());

        return "ProductionPlan/ProductPlanList";
    }


    @PostMapping("/save")
    public String saveProductionPlan(@ModelAttribute ProductionPlanDTO productionPlanDTO,
                                     @RequestParam("productionDate") List<LocalDate> productionDates,
                                     @RequestParam("quantityPerDay") List<Integer> QuantityPerDays) {

        String productionPlanCode;
        if (productionPlanDTO.getPlanId() == null) {
            // 등록 동작
            productionPlanCode = productionPlanService.registerProductionPlan(productionPlanDTO);
        } else {
            // 수정 동작
            productionPlanCode = productionPlanService.updateProductionPlan(productionPlanDTO);
            //먼저 해당 생산 계획으로 등록된 일자별 생산 내용을 지워야함
            List<ProductionPerDay> perDays = productionPerDayRepository.findByProductionId(productionPlanDTO.getPlanId());
            productionPerDayRepository.deleteAll(perDays);

        }

        //날짜 등록
        int compareDay = productionPlanDTO.getProductionEndDate().compareTo(productionPlanDTO.getProductionStartDate());
        log.info(compareDay);

        for (int i = 0; i < compareDay + 1; i++) {
            //수량이 0이거나 그보다 클 때만 등록(마이너스일 때는 등록되지 않는다)
            if (QuantityPerDays.get(i) >= 0) {
                ProductionPerDayDTO perDayDTO = ProductionPerDayDTO.builder()
                        .productionPlanCode(productionPlanCode)
                        .productionDate(productionDates.get(i))
                        .productionQuantity(QuantityPerDays.get(i))
                        .build();

                productionPerDayService.register(perDayDTO);
            }
        }


        return "redirect:/plan/list";
    }

    //수정하는 페이지
    @GetMapping("/edit/{id}")
    public String editProductionPlan(@PathVariable("id") Long id, Model model) {
        ProductionPlanDTO planDTO = productionPlanService.getProductionPlanById(id);
        model.addAttribute("productionPlanDTO", planDTO);

        List<ProductionPerDayDTO> perDayDTOS = productionPerDayService.findbyPlanId(id);
        log.info(perDayDTOS);
        model.addAttribute("perDayDTOS", perDayDTOS);

        List<BomDTO> bomDTOS = bomService.findWithProductCode(planDTO.getProductCode());
        model.addAttribute("bomDTOS", bomDTOS);

        return "ProductionPlan/ProductPlanModify";
    }

    @GetMapping("/delete/{id}")
    public String deleteProductionPlan(@PathVariable("id") Long id) {
        productionPlanService.deleteProductionPlan(id);
        return "redirect:/plan/list"; // 삭제 후 목록으로 리다이렉트
    }

    @GetMapping("/procureRegister")
    public String getProductionPlanCheck(Model model,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "id", required = false) Long id,
                                         @RequestParam(value = "productCode", required = false) String productCode,
                                         @RequestParam(value = "materialCode", required = false) String materialCode) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductionPlanDTO> plans;

        // 생산 계획 목록 조회
        if (keyword != null && !keyword.isEmpty()) {
            plans = productionPlanService.searchProductionPlans(keyword, pageable);
        } else {
            List<String> statuses = Arrays.asList("ON_HOLD", "IN_PROGRESS");
            plans = productionPlanService.getProductionPlansByStatus(statuses, pageable);
        }

        model.addAttribute("plans", plans.getContent());
        model.addAttribute("totalPages", plans.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        String productionPlanCode = null;

        // 선택된 생산 계획 처리
        if (id != null) {
            ProductionPlanDTO selectedPlan = productionPlanService.getProductionPlanById(id);
            model.addAttribute("selectedPlan", selectedPlan);
            productionPlanCode = selectedPlan.getProductionPlanCode();
        }

        // BOM 및 공급업체 정보 조회
        List<BomDTO> selectedBom = bomService.findWithProductCode(productCode);
        boolean allRegistered = true; // 모든 BOM이 등록되었는지 확인할 변수

        for (BomDTO bom : selectedBom) {
            // 각 BOM의 자재에 맞는 공급업체 조회
            List<SupplierDTO> suppliers = supplierService.findSuppliersByMaterialCode(bom.getMaterialCode());
            bom.setSuppliers(suppliers);

            // 각 BOM의 등록 여부를 체크
            boolean isRegistered = procurementPlanService.ProcurementCheckWithMaterialCodeAndProductionPlanCode(
                    bom.getMaterialCode(), productionPlanCode
            );
            bom.setRegister(isRegistered);

            // 하나라도 등록되지 않은 BOM이 있으면 allRegistered를 false로 설정
            if (!isRegistered) {
                allRegistered = false;
            }
        }

        model.addAttribute("selectedBom", selectedBom);
        model.addAttribute("productCode", productCode);

        // 모든 BOM이 등록되었을 경우 생산계획 상태를 FINISHED로 변경
        if (allRegistered && productionPlanCode != null) {
            productionPlanService.updateProductionPlanFinshed(productionPlanCode);
            System.out.println("Production Plan " + productionPlanCode + " 상태가 FINISHED로 변경되었습니다.");
        } else if (!allRegistered) {
            System.out.println("모든 BOM이 등록되지 않았으므로 FINISHED로 변경되지 않았습니다.");
        }

        return "procure/ProcureIndex";
    }

    @GetMapping("/bomRegister")
    public String bomRegister() {
        return "ProductionPlan/BomRegister";
    }
}