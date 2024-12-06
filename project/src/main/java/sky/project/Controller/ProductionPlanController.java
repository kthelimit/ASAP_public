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
import sky.project.DTO.BomDTO;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.DTO.SupplierDTO;
import sky.project.Service.BomService;
import sky.project.Service.MaterialService;
import sky.project.Service.ProductionPlanService;
import sky.project.Service.SupplierService;
import sky.project.ServiceImpl.ProcurementPlanServiceImpl;

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
    private ProcurementPlanServiceImpl procurementPlanServiceImpl;


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
    public String saveProductionPlan(@ModelAttribute ProductionPlanDTO productionPlanDTO) {
        if (productionPlanDTO.getPlanId() == null) {
            // 등록 동작
            productionPlanService.registerProductionPlan(productionPlanDTO);
        } else {
            // 수정 동작
            productionPlanService.updateProductionPlan(productionPlanDTO);
        }
        return "redirect:/plan/list";
    }

    @GetMapping("/edit/{id}")
    public String editProductionPlan(@PathVariable("id") Long id, Model model) {
        ProductionPlanDTO planDTO = productionPlanService.getProductionPlanById(id);
        model.addAttribute("productionPlanDTO", planDTO);
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
            plans = productionPlanService.getProductionPlans(pageable);
        }

        model.addAttribute("plans", plans.getContent());
        model.addAttribute("totalPages", plans.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        // 선택된 생산 계획 처리
        if (id != null) {
            ProductionPlanDTO selectedPlan = productionPlanService.getProductionPlanById(id);
            model.addAttribute("selectedPlan", selectedPlan);
        }


        // BOM 및 공급업체 정보 조회
        List<BomDTO> selectedBom = bomService.findWithProductCode(productCode);
        for (BomDTO bom : selectedBom) {
            // 각 BOM의 자재에 맞는 공급업체 조회
            List<SupplierDTO> suppliers = supplierService.findSuppliersByMaterialCode(bom.getMaterialCode());
            bom.setSuppliers(suppliers);
        }

        model.addAttribute("selectedBom", selectedBom);
        model.addAttribute("productCode", productCode);

        return "/procure/ProcureIndex";
    }


    @GetMapping("/procureHistory")
    public String getProductionPlanHistory(Model model,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("planId").descending());
        Page<ProcurementPlanDTO> procurementPlanDTOs;
        if (keyword != null && !keyword.isEmpty()) {
            procurementPlanDTOs = procurementPlanServiceImpl.searchProcurementPlans(keyword, pageable);
        } else {
            procurementPlanDTOs = procurementPlanServiceImpl.getProcurementPlans(pageable);
        }

        model.addAttribute("procurementPlans", procurementPlanDTOs.getContent());
        model.addAttribute("totalPages", procurementPlanDTOs.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);


        return "/procure/ProcureHistory";
    }

    @GetMapping("/bomRegister")
    public String bomRegister() {
        return "/ProductionPlan/BomRegister";
    }
}