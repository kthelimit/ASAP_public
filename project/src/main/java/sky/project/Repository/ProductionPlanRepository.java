package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.ProductionPlan;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {

    Page<ProductionPlan> findByProductNameContaining(String keyword, Pageable pageable);
}
