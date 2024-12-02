package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sky.project.Entity.ProcurementPlan;

import java.util.List;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Long> {

    // supplierName과 materialName 모두에서 검색
    Page<ProcurementPlan> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);

    @Query("select p from ProcurementPlan p where p.status='ON_HOLD' and (p.supplierName like %:keyword% or p.materialName like %:keyword%)")
    Page<ProcurementPlan> findBySupplierNameContainingOrMaterialNameContainingOnHold(String keyword, Pageable pageable);

    @Query("select p from ProcurementPlan p where p.status='ON_HOLD'")
    Page<ProcurementPlan> findAllOnHold(Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProcurementPlan p WHERE p.procurePlanCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("select p from ProcurementPlan p where p.procurePlanCode =:procurePlanCode")
    ProcurementPlan findByProcurePlanCode(String procurePlanCode);

    @Query("select count (p) from ProcurementPlan p where p.status='ON_HOLD'")
    int countProcurementPlanByStatusOnHold();

    @Query("SELECT COALESCE(SUM(p.requireQuantity), 0) FROM ProcurementPlan p WHERE p.materialCode = :materialCode")
    Integer findTotalRequireQuantityByMaterialCode(@Param("materialCode") String materialCode);


    @Query("SELECT p FROM ProcurementPlan p WHERE p.materialCode = :materialCode AND p.productionPlanCode = :productionPlanCode")
    List<ProcurementPlan> findByMaterialCodeAndProductionPlanCode(String materialCode, String productionPlanCode);
}
