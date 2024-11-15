package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/order/")
public class OrderController {

    @GetMapping("/index")
    public String index() {
        return "/Order/Orderindex";
    }

    @RequestMapping("/procure")
    public String procure() {
        return "/procure/Procureindex";
    }
}
