package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.GraphDTO;
import sky.project.DTO.InvoiceDTO;
import sky.project.Entity.Invoice;

import java.util.List;
import java.util.Map;

public interface InvoiceService {


    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);

    Page<InvoiceDTO> getAllInvoice(Pageable pageable);

    Page<InvoiceDTO> findInvoicesBySupplierName(String supplierName, Pageable pageable);

    InvoiceDTO getInvoiceById(Long invoiceId);

    // 특정 공급자의 모든 거래 명세서 조회
    List<InvoiceDTO> findInvoicesBySupplierName(String supplierName);

    // 연도별 거래 금액 요약
    Map<String, Double> getYearlySummary(String supplierName);

    // 월별 거래 금액 요약
    Map<String, Double> getMonthlySummary(String supplierName);

    // 주별 거래 금액 및 수량 요약
    Map<String, GraphDTO> getWeeklySummary(String supplierName);

    // 총 거래 금액 계산
    double getTotalAmount(String supplierName);


}
