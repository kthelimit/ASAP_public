package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import sky.project.DTO.ExportDTO;
import sky.project.DTO.OrdersDTO;
import sky.project.DTO.UserDTO;
import sky.project.Service.ExportService;
import sky.project.Service.OrderService;
import sky.project.Service.ProductionPlanService;
import sky.project.Service.SupplierService;

import java.util.List;

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

    @RequestMapping("/info")
    public String info(Model model) {
        return "Info/information";
    }


    @RequestMapping("/index")
    public String index(@SessionAttribute(name = "user", required = false) UserDTO user, Model model) {

        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }

        // userId로 supplierName 가져오기
        String userId = user.getUserId();
        String supplierName = supplierService.findSupplierNameByUserId(userId);

        //유저 타입 전송하기
        switch (user.getUserType()){
            case SUPPLIER -> model.addAttribute("userType", "SUPPLIER");
            case ADMIN -> model.addAttribute("userType", "ADMIN");
            case PARTNER -> model.addAttribute("userType", "PARTNER");
            case MATERIAL_DEPT -> model.addAttribute("userType", "MATERIAL_DEPT");
            case PRODUCTION_DEPT -> model.addAttribute("userType", "PRODUCTION_DEPT");
            case PURCHASE_DEPT -> model.addAttribute("userType", "PURCHASE_DEPT");
        }

        //승인 대기 중인 업체 수
        int countPartnerWhoWaitApproval = supplierService.getCountSupplierWhoWaitApproval();
        model.addAttribute("countPartnerWhoWait", countPartnerWhoWaitApproval);

        //진행 중인 생산 계획 수 - 현재 날짜에 해당되는 애들만.
        int countProductionPlan = productionPlanService.getCountProductionPlan();
        model.addAttribute("countProductionPlan", countProductionPlan);


        //이번 달 발주 건수
        int countOrder = orderService.getCountOrderThisMonth();
        model.addAttribute("countOrder", countOrder);

        //이번 달 발주 건수(업체용)
        int countOrderForSupplier = orderService.getCountOrderBySupplierThisMonth(supplierName);
        model.addAttribute("countOrderForSupplier", countOrderForSupplier);

        //이번 달 입고 건수

        //이번 달 입고 예정 건수

        //입고 검수 예정 건수

        //대기 중인 출고 요청 건수
        int countExportRequest = exportService.getCountCurrentRequest();
        model.addAttribute("countExportRequest", countExportRequest);

        //승인된 출고 요청 건수 (승인 혹은 종료인 것을 세어서 출력)
        int countExportApproved = exportService.getCountApprovedRequestThisMonth();
        model.addAttribute("countExportApproved", countExportApproved);

        //불출 완료된 건수 (종료인 것을 세어서 출력)
        int countExportFinished = exportService.getCountFinishedRequestThisMonth();
        model.addAttribute("countExportFinished", countExportFinished);

        // ★조달계획에 상태 추가해서 아직 발주 안 된 녀석들의 갯수를 세어와야함.
        //발주하지 않은 조달계획 수
        int countProcurementPlanNotYet = 1;
        model.addAttribute("countProcurementPlanNotYet", countProcurementPlanNotYet);

        //업체용 새 발주 건수
        int countOrdersForSupplier = orderService.getCountOrderBySupplierOnHOLD(supplierName);
        model.addAttribute("countOrdersForSupplier", countOrdersForSupplier);

        //발주 중인 목록(구매부서용)
        List<OrdersDTO> ordersDTOS = orderService.getRecentOrderList();
        model.addAttribute("ordersDTOS", ordersDTOS);

        //진행 중인 출고요청 목록(생산부서용)
        List<ExportDTO> exportDTOS = exportService.getRecentExportList();
        model.addAttribute("exportDTOS", exportDTOS);

        //입고 검수 중인 목록
        
        
        //발주 중인 목록(업체용 - 자기네 업체 관련 내용만 떠야함)
        List<OrdersDTO> ordersDTOSForSupplier = orderService.getRecentOrderListForSupplier(supplierName);
        model.addAttribute("ordersDTOSForSupplier", ordersDTOSForSupplier);



        return "Dashboard/Dashboard";
    }
}
