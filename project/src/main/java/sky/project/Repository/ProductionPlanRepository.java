package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.ProductionPlan;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {

    Page<ProductionPlan> findByProductNameContaining(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProductionPlan p WHERE p.productionPlanCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);
}
