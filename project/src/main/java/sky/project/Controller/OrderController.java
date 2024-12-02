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
import sky.project.DTO.DeliveryRequestDTO;
import sky.project.DTO.OrdersDTO;
import sky.project.DTO.ProcurementPlanDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Material;
import sky.project.Entity.Supplier;
import sky.project.Entity.Order;
import sky.project.Service.*;

import java.time.LocalDate;

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

    @GetMapping("/list")
    public String getProductionPlanList(Model model,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        @RequestParam(defaultValue = "1") int page2,
                                        @RequestParam(defaultValue = "5") int size2,
                                        @RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "procurePlanCode", required = false) String procurePlanCode,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "keyword", required = false) String keyword2) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable pageable2 = PageRequest.of(page2 - 1, size2);


            Page<ProcurementPlanDTO> procure;
            if (keyword != null && !keyword.isEmpty()) {
                procure = procurementPlanService.searchProcurementPlans(keyword, pageable);
            } else {
                procure = procurementPlanService.getAllProcurementPlan(pageable);
            }
            model.addAttribute("procure", procure.getContent());
            model.addAttribute("totalPages", procure.getTotalPages());

            // OrdersDTO 페이징 및 검색 처리
            Page<OrdersDTO> orders;
            if (keyword != null && !keyword.isEmpty()) {
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


        if (id != null) {
            ProcurementPlanDTO selectedOrder = procurementPlanService.getProcurementPlanById(id);
            Supplier supplier = supplierService.getSupplierByName(selectedOrder.getSupplierName());
            Material material = materialService.getMaterialByName(selectedOrder.getMaterialName());

            double unitPrice = material.getUnitPrice();
            int quantity = selectedOrder.getProcurementQuantity();
            double totalPrice = unitPrice * quantity;

            model.addAttribute("selectedOrder", selectedOrder);
            model.addAttribute("businessRegistrationNumber", supplier.getBusinessRegistrationNumber());
            model.addAttribute("supplierAddress", supplier.getAddress());
            model.addAttribute("orderDate", LocalDate.now());
            model.addAttribute("userName", supplier.getUser().getUsername());
            model.addAttribute("unitPrice", unitPrice);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("orderDTO",new OrdersDTO());
        }

        return "/Order/Orderindex";
    }



    @PostMapping("/register")
    public String OrderRegister(@ModelAttribute OrdersDTO ordersDTO) {
        // 데이터 확인
        System.out.println("Received OrdersDTO: " + ordersDTO);
        System.out.println("totalPrice : "+ordersDTO.getTotalPrice());

        // 여기서 orderDTO를 저장하거나 처리하는 로직 추가
        orderService.registerOrder(ordersDTO);

        // 등록 완료 후 목록 페이지로 리다이렉트
        return "redirect:/order/list";
    }

    @RequestMapping("/procure")
    public String procure() {
        return "/procure/Procureindex";
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
            @ModelAttribute("updatedOrder") OrdersDTO updatedOrderDTO) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable completedPageable = PageRequest.of(completedPage - 1, completedSize);
        Pageable deliveryPageable = PageRequest.of(deliveryPage - 1, deliverySize);

        // 처리 중인 주문 조회
        Page<OrdersDTO> purchaseOrders = orderService.findByStatus(CurrentStatus.APPROVAL.name(), pageable);

        // 처리 완료된 주문 조회
        Page<OrdersDTO> completedOrders = orderService.findByStatus(CurrentStatus.FINISHED.name(), completedPageable);

        // 납입 요청 조회
        Page<DeliveryRequestDTO> deliveryRequests = deliveryRequestService.findAll(deliveryPageable);

        // 최신 데이터 전달
        model.addAttribute("purchaseOrders", purchaseOrders.getContent());
        model.addAttribute("purchaseTotalPages", purchaseOrders.getTotalPages());
        model.addAttribute("purchaseCurrentPage", page);

        // 처리 완료된 주문 데이터 전달
        model.addAttribute("completedOrders", completedOrders.getContent());
        model.addAttribute("completedTotalPages", completedOrders.getTotalPages());
        model.addAttribute("completedCurrentPage", completedPage);

        // 납입 요청 데이터 전달
        model.addAttribute("deliveryRequests", deliveryRequests.getContent());
        model.addAttribute("deliveryTotalPages", deliveryRequests.getTotalPages());
        model.addAttribute("deliveryCurrentPage", deliveryPage);

        // 갱신된 주문 전달
        if (updatedOrderDTO != null) {
            model.addAttribute("updatedOrder", updatedOrderDTO);
        }

        return "/Order/DeliveryOrder";
    }

    @PostMapping("/delivery/request")
    public String requestDelivery(
            @RequestParam Long orderId,
            @RequestParam int requestedQuantity,
            RedirectAttributes redirectAttributes) {

        try {
            // 요청 처리 및 최신 DTO 반환
            OrdersDTO updatedOrderDTO = orderService.processDeliveryRequest(orderId, requestedQuantity);

            // DeliveryRequest 테이블에 요청 기록 저장
            deliveryRequestService.createRequest(orderId, requestedQuantity);

            // 성공 메시지 전달
            redirectAttributes.addFlashAttribute("success", "납입 요청이 성공적으로 처리되었습니다.");
            redirectAttributes.addFlashAttribute("updatedOrder", updatedOrderDTO); // 최신 DTO 전달
        } catch (IllegalArgumentException e) {
            // 에러 메시지 전달
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // 요청 후 리디렉션 처리 (DTO는 리디렉션 이후 갱신된 값 전달)
        return "redirect:/order/delivery";
    }



    @GetMapping("/inspection")
    public String inspectionRequest(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page - 1, size);

        // 제조품 조회
        Page<OrdersDTO> manufacturingOrders = orderService.findByMaterialTypeAndStatus(
                "제조품", CurrentStatus.APPROVAL.name(), pageable);
        System.out.println("제조품 내역확인"+manufacturingOrders.getContent());

        // 제조품 데이터 추가
        model.addAttribute("manufacturingOrders", manufacturingOrders.getContent());
        model.addAttribute("manufacturingTotalPages", manufacturingOrders.getTotalPages());
        model.addAttribute("manufacturingCurrentPage", page);


        return "/Order/ProgressInspection";
    }




}
