package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.SupplierStock;

import java.util.List;

public interface SupplierStockRepository extends JpaRepository<SupplierStock, Long> {

    @Query("select s from SupplierStock s where s.material.materialCode =:materialCode and s.supplier.supplierId =:supplierId")
    SupplierStock findByMaterialCodeWithSupplierId(String supplierId, String materialCode);

    @Query("select s from SupplierStock s where s.supplier.supplierId =:supplierId")
    List<SupplierStock> findBySupplierId(String supplierId);
}
