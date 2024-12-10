package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.DeliveryRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

    @Query("select d from DeliveryRequest d where d.supplier.supplierName=:supplierName")
    Page<DeliveryRequest> findBySupplierName(String supplierName, Pageable pageable);

    @Query("select count(d) from DeliveryRequest d where d.createdDate>=:start and d.createdDate<=:end")
    int countDeliveryRequestsThisMonth(LocalDateTime start, LocalDateTime end);

    @Query("select count(d) from DeliveryRequest d where d.createdDate>=:start and d.createdDate<=:end and d.supplier.supplierName=:supplierName")
    int countDeliveryRequestsThisMonth(LocalDateTime start, LocalDateTime end, String supplierName);

    @Query("select d from DeliveryRequest d where d.requestedDate<:requestedDate and d.status='IN_PROGRESS'")
    List<DeliveryRequest> findDeliveryRequestsByRequestedDateAndStatus(LocalDate requestedDate);

    @Query("select d from DeliveryRequest d where d.order.orderCode=:orderCode and d.createdDate<:createdDate")
    List<DeliveryRequest> findDeliveryRequestsByOrderCodeBefore(String orderCode, LocalDateTime createdDate);

    @Query("select d from DeliveryRequest d where d.order.orderCode=:orderCode")
    List<DeliveryRequest> findDeliveryRequestsByOrderCode(String orderCode);

    @Query("select d from DeliveryRequest d where d.order.orderCode =:orderCode and d.status='FINISHED'")
    List<DeliveryRequest> findDeliveryRequestsByOrderCodeFinished(String orderCode);

    @Query("select d from DeliveryRequest d where d.order.orderCode =:orderCode and d.status='DELIVERED'")
    List<DeliveryRequest> findDeliveryRequestsByOrderCodeDelivered(String orderCode);


    @Query("select d from DeliveryRequest d where d.order.orderCode =:orderCode and d.status='IN_PROGRESS'")
    List<DeliveryRequest> findDeliveryRequestsByOrderCodeInProgress(String orderCode);

    @Query("SELECT COUNT(d) FROM DeliveryRequest d WHERE d.deliveryCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

}