package sky.project.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/sample/")
public class SampleController {
    @GetMapping("/all1")
    public void exAll1() {
        log.info("exAll..............");
    }

    @GetMapping("/all2")
    public void exAll2() {
        log.info("exAll..............");
    }

    @GetMapping("/all3")
    public void exAll3() {
        log.info("exAll..............");
    }

    @GetMapping("/member")
    public void exMember() {
        log.info("exMember...............");
    }

    @GetMapping("/admin")
    public void exAdmin() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin2")
    public void exAdmin2() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin3")
    public void exAdmin3() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin4")
    public void exAdmin4() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin5")
    public void exAdmin5() {
        log.info("exAdmin..............");
    }

    @GetMapping("/babo")
    public void exBabo() {log.info("exBabo..............");}

    @GetMapping("/invoice")
    public void exInvoice() {
        log.info("exInvoice...............");
    }
}