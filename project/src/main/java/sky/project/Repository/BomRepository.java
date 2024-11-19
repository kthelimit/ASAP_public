package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Bom;

import java.util.List;

public interface BomRepository extends JpaRepository<Bom, Long> {

    @Query("select b from Bom b where b.product.productCode =:productCode")
    List<Bom> findByProductCode(String productCode);
}
