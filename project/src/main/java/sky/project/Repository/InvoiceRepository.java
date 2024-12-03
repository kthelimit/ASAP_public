package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findBySupplierName(String supplierName, Pageable pageable);
}
