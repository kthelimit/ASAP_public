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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.project.DTO.DeliveryRequestDTO;
import sky.project.DTO.InspectionDTO;
import sky.project.DTO.OrdersDTO;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Material;
import sky.project.Entity.Supplier;
import sky.project.Service.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@Log4j2
@RequestMapping("/order")
public class OrderController {

    @Autowired
    ProcurementPlanService procurementPlanService;

    @Autowired
    SupplierService supplierService;

    @Autowired
    MaterialService materialService;

    @Autowired
    OrderService orderService;

    @Autowired
    DeliveryRequestService deliveryRequestService;

    @Autowired
    InspectionService inspectionService;

    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "12") int size,
                                        @RequestParam(defaultValue = "1") int page2,
                                        @RequestParam(defaultValue = "5") int size2,
                                        @RequestParam(value = "id", required = false) Long id,
//                                        @RequestParam(value = "procurePlanCode", required = false) String procurePlanCode,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "keyword2", required = false) String keyword2) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable pageable2 = PageRequest.of(page2 - 1, size2, Sort.by("orderId").descending());


        Page<ProcurementPlanDTO> procure;
        if (keyword != null && !keyword.isEmpty()) {
            procure = procurementPlanService.searchProcurementPlansOnHold(keyword, pageable);
        } else {
            procure = procurementPlanService.getAllProcurementPlan(pageable);
        }
        model.addAttribute("procure", procure.getContent());
        model.addAttribute("totalPages", procure.getTotalPages());

        // OrdersDTO 페이징 및 검색 처리
        Page<OrdersDTO> orders;
        if (keyword2 != null && !keyword2.isEmpty()) {
            orders = orderService.searchOrders(keyword2, pageable2);
        } else {
            orders = orderService.getAllOrders(pageable2);
        }
        System.out.println("Orders from Service: " + orders.getContent());
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("totalPages2", orders.getTotalPages());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);


        model.addAttribute("currentPage2", page2);
        model.addAttribute("pageSize2", size2);
        model.addAttribute("keyword2", keyword2);

        for (ProcurementPlanDTO plan : procure.getContent()) {
            try {
                Material material = materialService.getMaterialByName(plan.getMaterialName());
                plan.setUnitPrice(material.getUnitPrice()); // DTO에 단가 설정
            } catch (RuntimeException e) {
                System.out.println("Material not found for name: " + plan.getMaterialName());
                plan.setUnitPrice(0); // 기본값 설정
            }
        }

            model.addAttribute("orderDate", LocalDate.now());
            model.addAttribute("orderDTO", new OrdersDTO());
            model.addAttribute("id", id);


            return "Order/Orderindex";
        }


    @GetMapping("/detail/{orderCode}")
    public String getOrderDetail(Model model, @PathVariable String orderCode) {

        //타임리프가 문자열에 쌍따옴표를 넣어줘서 그것을 제거하는 코드
        orderCode=orderCode.replace("\"","");

        OrdersDTO ordersDTO = orderService.findByOrderCode(orderCode);
        model.addAttribute("ordersDTO", ordersDTO);
        return "Order/OrderDetail";
    }


    @RequestMapping("/history")
    public String OrderHistory(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderId").descending());
        Page<OrdersDTO> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderService.searchOrders(keyword, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "Order/OrderHistory";
    }


    @PostMapping("/register")
    public String OrderRegister(
            @RequestParam("selectedOrders") List<String> selectedOrders,
            @RequestParam("procurePlanCode") List<String> procurePlanCodes,
            @RequestParam("supplierName") List<String> supplierNames,
            @RequestParam("materialName") List<String> materialNames,
            @RequestParam("unitPrice") List<Double> unitPrices,
            @RequestParam("orderQuantity") List<Integer> orderQuantities,
            @RequestParam("totalPrice") List<Double> totalPrices,
            @RequestParam("orderDate") List<String> orderDates,
            @RequestParam("expectedDate") List<LocalDate> expectedDate
    ) {
        // 데이터 확인
        for (int i = 0; i < selectedOrders.size(); i++) {
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setProcurePlanCode(procurePlanCodes.get(i));
            ordersDTO.setSupplierName(supplierNames.get(i));
            ordersDTO.setMaterialName(materialNames.get(i));
            ordersDTO.setUnitPrice(unitPrices.get(i));
            ordersDTO.setOrderQuantity(orderQuantities.get(i));
            ordersDTO.setTotalPrice(totalPrices.get(i));
            ordersDTO.setExpectedDate(expectedDate.get(i));
            ordersDTO.setOrderDate(LocalDate.parse(orderDates.get(i)));

            // 개별 DTO 처리
            System.out.println("Processing OrdersDTO: " + ordersDTO);
            orderService.registerOrder(ordersDTO);
        }

        // 등록 완료 후 목록 페이지로 리다이렉트
        return "redirect:/order/list";
    }


    @RequestMapping("/procure")
    public String procure() {
        return "procure/Procureindex";
    }


    @GetMapping("/delivery")
    public String progressPage(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") int completedPage,
            @RequestParam(defaultValue = "10") int completedSize,
            @RequestParam(defaultValue = "1") int deliveryPage,
            @RequestParam(defaultValue = "10") int deliverySize,
            @RequestParam(value = "keyword1", required = false) String keyword1,
            @RequestParam(value = "keyword2", required = false) String keyword2,
            @RequestParam(value = "keyword3", required = false) String keyword3,
            @ModelAttribute("updatedOrder") OrdersDTO updatedOrderDTO) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderId"));
        Pageable completedPageable = PageRequest.of(completedPage - 1, completedSize, Sort.by("orderId").descending());
        Pageable deliveryPageable = PageRequest.of(deliveryPage - 1, deliverySize, Sort.by("id").descending());

        // 처리 중인 주문 조회 (keyword1)
        Page<OrdersDTO> purchaseOrders;
        if (keyword1 != null && !keyword1.isEmpty()) {
            purchaseOrders = orderService.searchOrders(keyword1, pageable);
        } else {
            purchaseOrders = orderService.findByStatus(CurrentStatus.APPROVAL.name(), pageable);
        }

        // 추가 검색 조건 (keyword2)
        Page<OrdersDTO> additionalOrders1;
        if (keyword2 != null && !keyword2.isEmpty()) {
            additionalOrders1 = orderService.searchOrders(keyword2, pageable);
        } else {
            additionalOrders1 = Page.empty(pageable);
        }

        // 추가 검색 조건 (keyword3)
        Page<OrdersDTO> additionalOrders2;
        if (keyword3 != null && !keyword3.isEmpty()) {
            additionalOrders2 = orderService.searchOrders(keyword3, pageable);
        } else {
            additionalOrders2 = Page.empty(pageable);
        }

        List<String> statuses = Arrays.asList(
                CurrentStatus.IN_PROGRESS.name(),
                CurrentStatus.FINISHED.name(),
                CurrentStatus.DELIVERED.name());

        // 처리 완료된 주문 조회
        Page<OrdersDTO> completedOrders = orderService.findByStatuses(statuses, completedPageable);

        // 납입 요청 조회
        Page<DeliveryRequestDTO> deliveryRequests = deliveryRequestService.findAll(deliveryPageable);

        //내일을 배달 기본일로 넣음
        model.addAttribute("date", LocalDate.now().plusDays(1));

        // 최신 데이터 전달
        model.addAttribute("purchaseOrders", purchaseOrders.getContent());
        model.addAttribute("purchaseTotalPages", purchaseOrders.getTotalPages());
        model.addAttribute("purchaseCurrentPage", page);

        // 추가 검색 결과 전달
        model.addAttribute("additionalOrders1", additionalOrders1.getContent());
        model.addAttribute("additionalOrders2", additionalOrders2.getContent());

        // 처리 완료된 주문 데이터 전달
        model.addAttribute("completedOrders", completedOrders.getContent());
        model.addAttribute("completedTotalPages", completedOrders.getTotalPages());
        model.addAttribute("completedCurrentPage", completedPage);

        // 납입 요청 데이터 전달
        model.addAttribute("deliveryRequests", deliveryRequests.getContent());
        model.addAttribute("deliveryTotalPages", deliveryRequests.getTotalPages());
        model.addAttribute("deliveryCurrentPage", deliveryPage);

        // 검색 키워드 전달
        model.addAttribute("keyword1", keyword1);
        model.addAttribute("keyword2", keyword2);
        model.addAttribute("keyword3", keyword3);

        return "Order/DeliveryOrder";
    }


    @PostMapping("/delivery/request")
    public String requestDelivery(
          DeliveryRequestDTO dto, int page,
            RedirectAttributes redirectAttributes) {

        try {
            // 요청 처리 및 최신 DTO 반환
            OrdersDTO updatedOrderDTO = orderService.processDeliveryRequest(dto.getOrderId(), dto.getRequestedQuantity());

            // DeliveryRequest 테이블에 요청 기록 저장
            deliveryRequestService.createRequest(dto);

            // 성공 메시지 전달
            redirectAttributes.addFlashAttribute("success", "납입 요청이 성공적으로 처리되었습니다.");
            redirectAttributes.addFlashAttribute("updatedOrder", updatedOrderDTO); // 최신 DTO 전달
        } catch (IllegalArgumentException e) {
            // 에러 메시지 전달
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addAttribute("page", page);
        // 요청 후 리디렉션 처리 (DTO는 리디렉션 이후 갱신된 값 전달)
        return "redirect:/order/delivery";
    }


    @GetMapping("/inspection")
    public String inspectionRequest(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(value = "orderCode", required = false) String orderCode) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderId"));

        // 제조품 조회
        Page<OrdersDTO> manufacturingOrders = orderService.findByMaterialTypeAndStatus(
                "제조품", CurrentStatus.APPROVAL.name(), pageable);
        System.out.println("제조품 내역확인" + manufacturingOrders.getContent());

        // 제조품 데이터 추가
        model.addAttribute("manufacturingOrders", manufacturingOrders.getContent());
        model.addAttribute("manufacturingTotalPages", manufacturingOrders.getTotalPages());
        model.addAttribute("manufacturingCurrentPage", page);

        //진척 검수 테이블 추가
        if (orderCode != null) {
            OrdersDTO orderDTO = orderService.findByOrderCode(orderCode);
            model.addAttribute("orderDTO", orderDTO);
            List<InspectionDTO> inspectionDTOS = inspectionService.findByOrderCode(orderCode);
            model.addAttribute("inspectionDTOS", inspectionDTOS);

        }


        return "Order/ProgressInspection";
    }

    @GetMapping("/inspectionrequest")
    public String inspectionRequest(@RequestParam Long orderId) {
        orderService.updateOrderStatus(orderId, CurrentStatus.IN_PROGRESS);
        return "redirect:/order/delivery";
    }

    @PostMapping("/inspectionRegister")
    public String inspectionRegister(InspectionDTO dto, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("orderCode", dto.getOrderCode());
        inspectionService.register(dto);
        return "redirect:/order/inspection";
    }

    @PostMapping("/inspectionUpdate")
    public String inspectionUpdate(InspectionDTO dto) {
        inspectionService.updateInspection(dto);
        return "redirect:/suppliers/page#inspection";
    }

}
