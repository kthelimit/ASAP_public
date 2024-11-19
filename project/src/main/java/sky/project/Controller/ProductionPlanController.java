package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.project.DTO.BomDTO;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Service.BomService;
import sky.project.Service.ProductionPlanService;

@Controller
@Log4j2
@RequestMapping("/plan")
public class ProductionPlanController {


    @Autowired
    private ProductionPlanService productionPlanService;


    @Autowired
    private BomService bomService;


    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
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
    public String procureRegister() {
        return "/Procure/ProcureIndex";
    }


    @GetMapping("/bomRegister")
    public String bomRegister() {
        return "/ProductionPlan/BomRegister";
    }

    @PostMapping("/registerBom")
    public String RegisterBom(BomDTO dto, RedirectAttributes redirectAttributes) {
        bomService.register(dto);
        return "redirect:/plan/bomRegister";
    }
}