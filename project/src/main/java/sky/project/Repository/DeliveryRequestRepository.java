package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.DeliveryRequest;

import java.util.List;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

    Page<DeliveryRequest> findBySupplierName(String supplierName, Pageable pageable);

}