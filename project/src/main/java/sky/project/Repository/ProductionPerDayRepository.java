package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.ProductionPerDay;

import java.util.List;

public interface ProductionPerDayRepository extends JpaRepository<ProductionPerDay, Long> {

    @Query("select p from ProductionPerDay p where p.productionPlan.planId=:id")
    List<ProductionPerDay> findByProductionId(Long id);
}
