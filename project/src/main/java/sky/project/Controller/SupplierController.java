package sky.project.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.project.DTO.*;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.DeliveryRequest;
import sky.project.Entity.Supplier;
import sky.project.Entity.SupplierStock;
import sky.project.Repository.SupplierStockRepository;
import sky.project.Service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryRequestService deliveryRequestService;

    @Autowired
    private ImportService importService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SupplierStockRepository supplierStockRepository;

    @Autowired
    private InspectionService inspectionService;

    @GetMapping("/list")
    public String getSuppliersList(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "12") int size,
                                   @RequestParam(value = "keyword", required = false) String keyword) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SupplierDTO> supplier;
        if (keyword != null && !keyword.isEmpty()) {
            supplier = supplierService.searchSuppliers(keyword, pageable);
        } else {
            supplier = supplierService.getAllSuppliers(pageable);
        }

        model.addAttribute("suppliers", supplier.getContent());
        model.addAttribute("totalPages", supplier.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "Supplier/SupplierList";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }
        String userId = user.getUserId();

        if (userId != null && supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("alertMessage", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "redirect:/dashboard/index";
        }

        model.addAttribute("supplierDTO", new SupplierDTO());
        return "Supplier/SupplierRegister";
    }

    @PostMapping("/register")
    public String registerSupplier(@ModelAttribute SupplierDTO supplierDTO,
                                   @RequestParam("contractFile") MultipartFile file,
                                   HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }
        String userId = user.getUserId();
        supplierDTO.setSupplierId(userId);  // supplierId 설정

        if (supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("message", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "Supplier/SupplierRegister";
        }

        try {
            if (!file.isEmpty()) {
                String directoryPath = "C:/uploads/File";
                Path directory = Paths.get(directoryPath);
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }
                String filePath = directoryPath + "/" + file.getOriginalFilename();
                file.transferTo(new File(filePath));
                supplierDTO.setContractFilePath("/uploads/File" + file.getOriginalFilename());
            }
            supplierDTO.setUserId(userId);
            supplierService.registerSupplier(supplierDTO);
            return "redirect:/sample/admin";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 업로드에 실패했습니다.");
            return "Supplier/SupplierRegister";
        }
    }

    @GetMapping("/pending")
    public String showPendingApprovals(Model model,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        List<SupplierDTO> suppliers = supplierService.getPendingApprovals(page, size);
        int totalPages = (int) Math.ceil((double) supplierService.getTotalPendingCount() / size);


        model.addAttribute("suppliers", suppliers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "Supplier/PendingApprovals";
    }

    @PostMapping("/approve/{supplierId}")
    public String approveSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        try {
            supplierService.approveSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending";
        }
        return "redirect:/suppliers/pending";
    }

    @PostMapping("/reject/{supplierId}")
    public String rejectSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        try {
            supplierService.rejectSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending";
        }
        return "redirect:/suppliers/pending";
    }

    @GetMapping("/detail/{id}")
    public String getSupplierDetail(@PathVariable String id, Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        // 협력사 정보 가져오기
        SupplierDTO supplierDTO = supplierService.getSupplierById(id);
        model.addAttribute("supplierDTO", supplierDTO);

        // 거래 요약
        double totalAmount = invoiceService.getTotalAmount(supplierDTO.getSupplierName());
        model.addAttribute("totalAmount", totalAmount);

        // 취급 자재 품목
        model.addAttribute("supplierStocks", supplierStockService.findBySupplierId(supplierDTO.getSupplierId()));

        // 거래 명세서 (페이지네이션 적용)
        Pageable invoicePageable = PageRequest.of(page - 1, size);
        Page<InvoiceDTO> invoices = invoiceService.findInvoicesBySupplierName(supplierDTO.getSupplierName(), invoicePageable);
        model.addAttribute("invoices", invoices.getContent());
        model.addAttribute("TotalPages", invoices.getTotalPages());
        model.addAttribute("CurrentPage", page);
        model.addAttribute("size", size);

        return "Supplier/SupplierDetail";
    }



    @PostMapping("/stockUpdate")
    public String supplierStockUpdate(SupplierStockDTO dto) {
        supplierStockService.updateStock(dto);
        return "redirect:/suppliers/page";
    }

    @GetMapping("/page")
    public String supplierPage(
            @SessionAttribute(name = "user", required = false) UserDTO user,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "1") int deliveryPage,
            @RequestParam(defaultValue = "5") int deliverySize,
            @RequestParam(defaultValue = "1") int invoicePage,
            @RequestParam(defaultValue = "5") int invoiceSize,
            @RequestParam(defaultValue = "1") int inspectionPage,
            @RequestParam(defaultValue = "5") int inspectionSize
    ) {

        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }

        // 유효성 검사
        if (page < 1 || deliveryPage < 1 || invoicePage < 1 || inspectionPage < 1) {
            model.addAttribute("message", "잘못된 페이지 요청입니다.");
            return "redirect:/";
        }

        // userId로 supplierName 가져오기
        String userId = user.getUserId();
        String supplierName = supplierService.findSupplierNameByUserId(userId);
        if (supplierName == null || supplierName.isEmpty()) {
            model.addAttribute("message", "공급자 정보를 찾을 수 없습니다.");
            return "redirect:/";
        }


        // supplierName으로 SupplierDTO 가져오기
        Supplier supplier = supplierService.getSupplierByName(supplierName);

        model.addAttribute("supplier", supplier);
        //supplier 정보 가져와서 출력해주기
        SupplierDTO supplierDTO = supplierService.getSupplierById(userId);
        model.addAttribute("supplierDTO", supplierDTO);

        //진척 검수 요청 가져와서 출력해주기
        Pageable inspectionPageable = PageRequest.of(inspectionPage - 1, inspectionSize);
        Page<InspectionDTO> inspections = inspectionService.findBySupplierName(supplierName, inspectionPageable);

        // supplierName으로 OrdersDTO 가져오기
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OrdersDTO> orderRequests = orderService.findOrdersBySupplier(supplierName, pageable);

        // supplierName으로 DeliveryRequestDTO 가져오기
        Pageable deliveryPageable = PageRequest.of(deliveryPage - 1, deliverySize);
        Page<DeliveryRequestDTO> deliveryRequests = deliveryRequestService.findRequestsBySupplier(supplierName, deliveryPageable);

        // supplierName으로 InvoiceDTO 가져오기
        Pageable invoicePageable = PageRequest.of(invoicePage - 1, invoiceSize);
        Page<InvoiceDTO> invoices = invoiceService.findInvoicesBySupplierName(supplierName, invoicePageable);

        // Thymeleaf에 데이터 추가
        model.addAttribute("supplierName", supplierName);
        model.addAttribute("orderRequests", orderRequests.getContent());
        model.addAttribute("totalOrderPages", orderRequests.getTotalPages());
        model.addAttribute("currentOrderPage", page);

        model.addAttribute("supplierStocks", supplierStockService.findBySupplierId(userId));
        model.addAttribute("deliveryRequests", deliveryRequests.getContent());
        model.addAttribute("deliveryTotalPages", deliveryRequests.getTotalPages());
        model.addAttribute("deliveryCurrentPage", deliveryPage);

        // Invoice 데이터 추가
        model.addAttribute("invoices", invoices.getContent());
        model.addAttribute("invoiceTotalPages", invoices.getTotalPages());
        model.addAttribute("invoiceCurrentPage", invoicePage);

        //Inspection 데이터 추가
        model.addAttribute("inspectionDTOS", inspections.getContent());
        model.addAttribute("inspectionTotalPages", inspections.getTotalPages());
        model.addAttribute("inspectionCurrentPage", inspectionPage);


        // 거래 내역 요약 데이터 추가
        double totalAmount = invoiceService.getTotalAmount(supplierName);
        Map<Integer, Double> yearlySummary = invoiceService.getYearlySummary(supplierName);
        Map<String, Double> monthlySummary = invoiceService.getMonthlySummary(supplierName);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("yearlySummary", yearlySummary);
        model.addAttribute("monthlySummary", monthlySummary);


        return "Supplier/SupplierPage";
    }


    @PostMapping("/orders/approve")
    public String approveOrder(@RequestParam Long orderId) {
        orderService.updateOrderStatus(orderId, CurrentStatus.APPROVAL);
        return "redirect:/suppliers/page";
    }

    @PostMapping("/orders/reject")
    public String rejectOrder(@RequestParam Long orderId) {
        orderService.updateOrderStatus(orderId, CurrentStatus.REJECT);
        return "redirect:/suppliers/page";
    }

    @PostMapping("/importregisterbatch")
    public String handleBatchExport(@ModelAttribute ImportDTO importDTO, @RequestParam("selectedRequests") List<Long> selectedRequestIds) {
        for (Long id : selectedRequestIds) {
            // DeliveryRequest 가져오기
            DeliveryRequest deliveryRequest = deliveryRequestService.findById(id);

            if (deliveryRequest == null) {
                throw new RuntimeException("DeliveryRequest not found for ID: " + id);
            }

            // Import 데이터 저장
            importDTO.setOrderCode(deliveryRequest.getOrder().getOrderCode());
            importDTO.setMaterialName(deliveryRequest.getMaterial().getMaterialName());
            importDTO.setSupplierName(deliveryRequest.getSupplier().getSupplierName());
            importDTO.setQuantity(deliveryRequest.getRequestedQuantity());
            importDTO.setImportStatus(CurrentStatus.ON_HOLD);
            importDTO.setOrderedQuantity(deliveryRequest.getRequestedQuantity());
            importService.createImport(importDTO);

            // InvoiceDTO 생성 및 필드 설정
            InvoiceDTO invoiceDTO = new InvoiceDTO();
            invoiceDTO.setSupplierName(deliveryRequest.getSupplier().getSupplierName());
            invoiceDTO.setMaterialName(deliveryRequest.getMaterial().getMaterialName());
            invoiceDTO.setQuantity(deliveryRequest.getRequestedQuantity());
            invoiceService.createInvoice(invoiceDTO);

            // DeliveryRequest 상태 업데이트(배달중으로 바꿈)
            deliveryRequestService.updateRequestStatus(id, "FINISHED");
            // 업체 재고에서 뺀다.
            SupplierStock stock = supplierStockRepository.findBySupplierNameAndMaterialCode(deliveryRequest.getSupplier().getSupplierName(), deliveryRequest.getMaterial().getMaterialCode());
            stock.setStock(stock.getStock() - deliveryRequest.getRequestedQuantity());
            supplierStockRepository.save(stock);

            // 발주서의 모든 조달 수량을 만족한 경우 주문 상태 업데이트
            String orderCode = deliveryRequest.getOrder().getOrderCode();
            OrdersDTO order = orderService.findByOrderCode(orderCode);
            List<DeliveryRequest> deliveryRequestsInSameOrder = deliveryRequestService.findByFinishedRequests(orderCode);
            int sum = 0;
            for (DeliveryRequest deliveryRequestInSameOrder : deliveryRequestsInSameOrder) {
                sum += deliveryRequestInSameOrder.getRequestedQuantity();
            }
            if (order.getOrderQuantity() <= sum) {
                orderService.updateOrderStatus(order.getOrderId(), CurrentStatus.DELIVERED);
            }

        }
        return "redirect:/suppliers/page";
    }


}