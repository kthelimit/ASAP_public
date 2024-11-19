package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
