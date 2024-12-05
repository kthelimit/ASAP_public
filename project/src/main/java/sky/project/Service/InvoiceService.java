package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.InvoiceDTO;
import sky.project.Entity.Invoice;

public interface InvoiceService {


    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);

    Page<InvoiceDTO> getAllInvoice(Pageable pageable);

    Page<InvoiceDTO> findInvoicesBySupplierName(String supplierName, Pageable pageable);

    InvoiceDTO getInvoiceById(Long invoiceId);
}
