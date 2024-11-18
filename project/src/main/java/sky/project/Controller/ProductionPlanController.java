package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Service.ProductionPlanService;

@Controller
@Log4j2
@RequestMapping("/plan")
public class ProductionPlanController {


    @Autowired
    private ProductionPlanService productionPlanService;

    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductionPlanDTO> plans;

        // 검색어가 있을 경우 검색 결과 반환, 없으면 전체 목록 반환
        if (keyword != null && !keyword.isEmpty()) {
            plans = productionPlanService.searchProductionPlans(keyword, pageable);
        } else {
            plans = productionPlanService.getProductionPlans(pageable);
        }

        model.addAttribute("plans", plans.getContent());
        model.addAttribute("totalPages", plans.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);  // 검색어 추가

        // 등록 폼에 사용될 빈 ProductionPlanDTO 설정
        model.addAttribute("productionPlanDTO", new ProductionPlanDTO());

        return "ProductionPlan/ProductPlanList"; // 생산 계획 목록과 등록 폼이 있는 동일 페이지
    }

    @PostMapping("/register")
    public String registerProductionPlan(@ModelAttribute ProductionPlanDTO productionPlanDTO) {
        productionPlanService.registerProductionPlan(productionPlanDTO);
        return "redirect:/plan/list";
    }












    @GetMapping("/procureRegister")
    public String procureRegister() {
        return "/Procure/ProcureIndex";
    }
}