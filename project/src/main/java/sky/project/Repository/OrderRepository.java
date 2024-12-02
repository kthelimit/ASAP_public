package sky.project.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);
    Page<Order> findBySupplierName(String supplierName, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN Material m ON o.materialName = m.materialName " +
            "WHERE m.materialType = :materialType AND o.status = :status")
    Page<Order> findByMaterialTypeAndStatus(@Param("materialType") String materialType,
                                            @Param("status") CurrentStatus status,
                                            Pageable pageable);


    Page<Order> findByStatus(CurrentStatus status, Pageable pageable);

    //대시보드 출력용 이번달 발주 건수
    @Query("select count(o) from Order o where o.createdDate>=:start and o.createdDate<=:end")
    int countOrderThisMonth(LocalDateTime start, LocalDateTime end);

    //대시보드 출력용 업체에 들어온 발주 건수
    @Query("select count(o) from Order o where o.status= 'ON_HOLD' and o.supplierName=:supplierName")
    int countOrderBySupplierName(String supplierName);


    @Query("SELECT COALESCE(SUM(o.orderQuantity), 0) FROM Order o " +
            "WHERE o.supplierName = :supplierName AND o.materialName = :materialName " +
            "AND o.status IN (:statuses)")
    int findApprovedQuantity(@Param("supplierName") String supplierName,
                             @Param("materialName") String materialName,
                             @Param("statuses") List<CurrentStatus> statuses);


}

