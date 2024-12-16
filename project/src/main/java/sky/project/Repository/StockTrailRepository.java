package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.StockTrail;

public interface StockTrailRepository extends JpaRepository<StockTrail, Long> {
}
