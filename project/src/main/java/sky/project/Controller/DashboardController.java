package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.project.Service.ExportService;
import sky.project.Service.OrderService;
import sky.project.Service.ProductionPlanService;
import sky.project.Service.SupplierService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    SupplierService supplierService;

    @Autowired
    ProductionPlanService productionPlanService;

    @Autowired
    ExportService exportService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/index")
    public String index(Model model) {

        //승인 대기 중인 업체 수
        int countPartnerWhoWaitApproval = supplierService.getCountSupplierWhoWaitApproval();
        model.addAttribute("countPartnerWhoWait", countPartnerWhoWaitApproval);

        //진행 중인 생산 계획 수 - 현재 날짜에 해당되는 애들만.
        int countProductionPlan = productionPlanService.getCountProductionPlan();
        model.addAttribute("countProductionPlan", countProductionPlan);


        //이번 달 발주 건수
        int countOrder = orderService.getCountOrderThisMonth();
        model.addAttribute("countOrder", countOrder);


        //이번 달 입고 건수

        //이번 달 입고 예정 건수

        //입고 검수 예정 건수

        //대기 중인 출고 요청 건수
        int countExportRequest = exportService.getCountCurrentRequest();
        model.addAttribute("countExportRequest", countExportRequest);

        //승인된 출고 요청 건수
        int countExportApproved = exportService.getCountApprovedRequestThisMonth();
        model.addAttribute("countExportApproved", countExportApproved);

        //불출 완료된 건수 - 승인된 출고 요청 중 승인된 날짜 + 1일이 지난 것들을 출력한다.
        int countExportFinished = exportService.getCountFinishedRequestThisMonth();
        model.addAttribute("countExportFinished", countExportFinished);

        // ★조달계획에 상태 추가해서 아직 발주 안 된 녀석들의 갯수를 세어와야함.
        //발주하지 않은 조달계획 수
        int countProcurementPlanNotYet=1;
        model.addAttribute("countProcurementPlanNotYet",countProcurementPlanNotYet);


        return "Dashboard/Dashboard";
    }
}
