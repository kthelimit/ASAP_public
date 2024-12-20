package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.ProductionPlan;

import java.time.LocalDate;
import java.util.List;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {

    Page<ProductionPlan> findByProductNameContaining(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProductionPlan p WHERE p.productionPlanCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("select pp from ProductionPlan pp where pp.productionPlanCode=:productionPlanCode")
    ProductionPlan findByProductionPlanCode(String productionPlanCode);


    @Query("select pp from ProductionPlan pp where pp.productionStartDate<=:planDate and pp.productionEndDate>=:planDate")
    List<ProductionPlan> findByPlanDate(LocalDate planDate);

    @Query("select pp from ProductionPlan pp where pp.status='FINISHED' and pp.productCode=:productCode")
    List<ProductionPlan> findPlanFinished(String productCode);

    //대시 보드 출력용 현재 계획
    @Query("select count(pp) from ProductionPlan pp where pp.productionStartDate<=:planDate and pp.productionEndDate>=:planDate")
    int countCurrentPlan(LocalDate planDate);

    Page<ProductionPlan> findByStatusIn(List<String> statuses, Pageable pageable);
    }

