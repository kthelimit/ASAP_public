package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import sky.project.DTO.ExportDTO;
import sky.project.DTO.ImportDTO;
import sky.project.DTO.OrdersDTO;
import sky.project.DTO.UserDTO;
import sky.project.Service.*;

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

    @Autowired
    ProcurementPlanService procurementPlanService;

    @Autowired
    DeliveryRequestService deliveryRequestService;

    @Autowired
    InspectionService inspectionService;

    @Autowired
    ImportService importService;

    @Autowired
    ReturnService returnService;

    @RequestMapping("/info")
    public String info(Model model) {
        return "Info/information";
    }

    @RequestMapping("/dataUpload")
    public String dataUpload(Model model) {
        return "Info/DataUpload";
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
        switch (user.getUserType()) {
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

        //이번 달 납품 지시 건수
        int countDeliveryRequest = deliveryRequestService.getCountRequestThisMonth();
        model.addAttribute("countDeliveryRequest", countDeliveryRequest);

        //이번 달 납품 지시 건수(업체용)
        int countDeliveryRequestForSupplier = deliveryRequestService.getCountRequestThisMonth(supplierName);
        model.addAttribute("countDeliveryRequestForSupplier", countDeliveryRequestForSupplier);

        //아직 처리하지 않은 납품 지시 건수(업체용)
        int countDeliveryRequestForSupplierNotYet = deliveryRequestService.getCountRequestNotYet(supplierName);
        model.addAttribute("countDeliveryRequestForSupplierNotYet", countDeliveryRequestForSupplierNotYet);

        //불량품 재배송 건수(업체용)
        int countReturnNotFinished = returnService.getCountReturnNotFinished(supplierName);
        model.addAttribute("countReturnNotFinished", countReturnNotFinished);

        //입고 예정 건수
        int countImport= importService.getCountImportOnHold();
        model.addAttribute("countImport", countImport);


        //입고 검수 예정 건수
        int countImportInspection = importService.getCountImportInInspection();
        model.addAttribute("countImportInspection", countImportInspection);


        //진척 검수일 체크(업체용)
        boolean isInspectionDate = inspectionService.checkInspectionDate();
        model.addAttribute("isInspectionDate", isInspectionDate);


        //진행 중인 진척 검수 건수(업체용)
        int countInspectionForSupplier = inspectionService.getCountInspectionForSupplier(supplierName);
        model.addAttribute("countInspectionForSupplier", countInspectionForSupplier);

        //대기 중인 출고 요청 건수
        int countExportRequest = exportService.getCountCurrentRequest();
        model.addAttribute("countExportRequest", countExportRequest);

        //승인된 출고 요청 건수 (승인 혹은 종료인 것을 세어서 출력)
        int countExportApproved = exportService.getCountApprovedRequestThisMonth();
        model.addAttribute("countExportApproved", countExportApproved);

        //불출 완료된 건수 (종료인 것을 세어서 출력)
        int countExportFinished = exportService.getCountFinishedRequestThisMonth();
        model.addAttribute("countExportFinished", countExportFinished);

        //발주하지 않은 조달계획 수
        int countProcurementPlanNotYet = procurementPlanService.getCountProcurementPlanOnHold();
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

        //진행 중인 입고 목록(자재부서용)

        List<ImportDTO> importDTOS = importService.getRecentImportList();
        model.addAttribute("importDTOS", importDTOS);

        //발주 중인 목록(업체용 - 자기네 업체 관련 내용만 떠야함)
        List<OrdersDTO> ordersDTOSForSupplier = orderService.getRecentOrderListForSupplier(supplierName);
        model.addAttribute("ordersDTOSForSupplier", ordersDTOSForSupplier);



        return "Dashboard/Dashboard";
    }
}
