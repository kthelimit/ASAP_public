package sky.project.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);
    Page<Order> findBySupplierName(String supplierName, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN Material m ON o.materialName = m.materialName " +
            "WHERE m.materialType = :materialType AND o.status = :status")
    Page<Order> findByMaterialTypeAndStatus(@Param("materialType") String materialType,
                                            @Param("status") CurrentStatus status,
                                            Pageable pageable);
}

