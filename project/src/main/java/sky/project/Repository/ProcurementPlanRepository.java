package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.project.Entity.ProcurementPlan;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Long> {

    // supplierName과 materialName 모두에서 검색
    Page<ProcurementPlan> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProcurementPlan p WHERE p.procurePlanCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);
}
