package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Service.ProcurementPlanService;
import org.springframework.ui.Model;

@Controller
@Log4j2
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    ProcurementPlanService procurementPlanService;

    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProcurementPlanDTO> procure;

        if (keyword != null && !keyword.isEmpty()) {
            procure = procurementPlanService.searchProcurementPlans(keyword, pageable);
        } else {
            procure = procurementPlanService.getAllProcurementPlan(pageable);
        }

        model.addAttribute("procure",procure.getContent());
        model.addAttribute("totalPages", procure.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);


        return "/Order/Orderindex";
    }
    @RequestMapping("/procure")
    public String procure() {
        return "/procure/Procureindex";
    }


    @GetMapping("/delivery")
    public String delivery() {
        return "/Order/DeliveryOrder";


    }
}
