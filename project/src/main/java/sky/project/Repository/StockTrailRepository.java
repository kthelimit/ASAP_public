package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.project.Entity.StockTrail;

import java.util.List;

public interface StockTrailRepository extends JpaRepository<StockTrail, Long> {

    // Material 엔티티의 materialCode 속성을 기준으로 StockTrail 조회
    @Query("SELECT s FROM StockTrail s WHERE s.material.materialCode = :materialCode")
    List<StockTrail> findByMaterialCode(@Param("materialCode") String materialCode);
}
