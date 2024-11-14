package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/plan")
public class ProductionPlanController {

    @GetMapping({"/", ""})
    public String index() {
        return "/ProductionPlan/ProductPlanIndex";
    }
}
