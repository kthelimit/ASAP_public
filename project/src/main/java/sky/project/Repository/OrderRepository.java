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
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.supplier.supplierName=:supplierName and o.material.materialName=:materialName")
    Page<Order> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);

    @Query("select o from Order o where o.supplier.supplierName=:supplierName")
    Page<Order> findBySupplierName(String supplierName, Pageable pageable);

    @Query("select o from Order o where o.supplier.supplierName=:supplierName")
    List<Order> findBySupplierName(String supplierName);

    @Query("SELECT o FROM Order o JOIN Material m ON o.material.materialName = m.materialName " +
            "WHERE m.materialType = :materialType AND o.status = :status")
    Page<Order> findByMaterialTypeAndStatus(@Param("materialType") String materialType,
                                            @Param("status") CurrentStatus status,
                                            Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);


    Page<Order> findByStatus(CurrentStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses")
    Page<Order> findByStatuses(@Param("statuses") List<CurrentStatus> statuses, Pageable pageable);

    @Query("select o from Order o where o.material.materialCode=:materialCode and o.status !='FINISHED'")
    List<Order> findByMaterialCodeNotFinished(String materialCode);

    //대시보드 출력용 이번달 발주 건수
    @Query("select count(o) from Order o where o.createdDate>=:start and o.createdDate<=:end")
    int countOrderThisMonth(LocalDateTime start, LocalDateTime end);

    //대시보드 출력용 이번달 업체에 들어온 발주 건수
    @Query("select count(o) from Order o where o.supplier.supplierName=:supplierName and o.createdDate>=:start and o.createdDate<=:end")
    int countOrderBySupplierName(String supplierName, LocalDateTime start, LocalDateTime end);

    //대시보드 출력용 업체에 들어온 새 발주 건수
    @Query("select count(o) from Order o where o.status= 'ON_HOLD' and o.supplier.supplierName=:supplierName")
    int countOrderBySupplierNameOnHOLD(String supplierName);

    @Query("SELECT COALESCE(SUM(o.orderQuantity), 0) FROM Order o " +
            "WHERE o.supplier.supplierName = :supplierName AND o.material.materialName = :materialName " +
            "AND o.status IN (:statuses)")
    int findApprovedQuantity(@Param("supplierName") String supplierName,
                             @Param("materialName") String materialName,
                             @Param("statuses") List<CurrentStatus> statuses);


    //대시보드 출력용 최근 발주리스트

    @Query("select o from Order o order by o.orderId desc limit 5")
    List<Order> findRecentOrder();

    //대시보드 출력용 최근 발주리스트(업체용)

    @Query("select o from Order o where o.supplier.supplierName=:supplierName order by o.orderId desc limit 5")
    List<Order> findRecentOrderForSupplier(String supplierName);


    Optional<Order> findByOrderCode(String orderCode);

}

