package sky.project.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sky.project.DTO.InvoiceDTO;
import sky.project.Entity.Invoice;
import sky.project.Entity.Supplier;
import sky.project.Service.InvoiceService;
import org.springframework.ui.Model;
import sky.project.Service.SupplierService;

import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    SupplierService supplierService;


    @GetMapping("/list")
    public String invoiceList(
            @RequestParam(defaultValue = "1") int page, // 현재 페이지 번호 (1부터 시작)
            @RequestParam(defaultValue = "10") int size, // 페이지 당 데이터 개수
            Model model) {

        // Pageable 객체 생성 (page는 0-based index로 변환)
        Pageable pageable = PageRequest.of(page - 1, size);

        // InvoiceDTO 리스트 가져오기
        Page<InvoiceDTO> invoices = invoiceService.getAllInvoice(pageable);

        // 뷰로 데이터 전달
        model.addAttribute("invoices", invoices.getContent()); // 현재 페이지 데이터
        model.addAttribute("totalPages", invoices.getTotalPages()); // 총 페이지 수
        model.addAttribute("currentPage", page); // 현재 페이지 번호

        return "Invoice/InvoiceList"; // Thymeleaf 뷰 이름
    }


    @GetMapping("/detail/{id}")
    public String getInvoiceDetail(@PathVariable Long id, Model model) {
        // 단일 거래 명세서 가져오기
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);

        // InvoiceDTO에서 supplierName 가져오기
        String supplierName = invoice.getSupplierName();

        // 공급자 정보 조회
        Supplier supplier = supplierService.getSupplierByName(supplierName);

        // Model에 추가
        model.addAttribute("invoice", invoice);
        model.addAttribute("supplier", supplier);

        return "Invoice/InvoiceDetailView"; // 뷰 이름
    }



}




