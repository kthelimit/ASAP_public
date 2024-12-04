package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.DeliveryRequest;

import java.time.LocalDateTime;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

    Page<DeliveryRequest> findBySupplierName(String supplierName, Pageable pageable);

    @Query("select count(d) from DeliveryRequest d where d.createdDate>=:start and d.createdDate<=:end")
    int countDeliveryRequestsThisMonth(LocalDateTime start, LocalDateTime end);

    @Query("select count(d) from DeliveryRequest d where d.createdDate>=:start and d.createdDate<=:end and d.supplierName=:supplierName")
    int countDeliveryRequestsThisMonth(LocalDateTime start, LocalDateTime end, String supplierName);
}