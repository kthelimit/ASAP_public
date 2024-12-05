package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Invoice;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findBySupplierName(String supplierName, Pageable pageable);

    List<Invoice> findBySupplierName(String supplierName);

}
