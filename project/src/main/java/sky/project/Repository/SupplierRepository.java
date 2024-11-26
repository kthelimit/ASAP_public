package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.project.Entity.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, String> {
    boolean existsByUserUserId(String userId);

    Page<Supplier> findByApprovedFalse(PageRequest pageable);

    Page<Supplier> findByApprovedTrue(Pageable pageable);

    List<Supplier> findByApprovedTrue();

    Optional<Supplier> findByUser_UserId(String userId);

    long countByApprovedFalse();

    // 승인 상태를 업데이트하는 메서드

    @Query("select s.supplierId from Supplier s where s.supplierName=:supplierName")
    String findSupplierIdBySupplierName(String supplierName);

    @Query("SELECT s FROM Supplier s JOIN s.materials m WHERE m.materialCode = :materialCode")
    List<Supplier> findSuppliersByMaterialCode(@Param("materialCode") String materialCode);

    Page<Supplier> findBySupplierNameContaining(String supplierName, Pageable pageable);

    Supplier findBySupplierName(String supplierName);
}
