package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
