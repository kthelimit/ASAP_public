package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.SupplierStock;

import java.util.List;
import java.util.Optional;

public interface SupplierStockRepository extends JpaRepository<SupplierStock, Long> {

    @Query("select s from SupplierStock s where s.material.materialCode =:materialCode and s.supplier.supplierId =:supplierId")
    SupplierStock findByMaterialCodeWithSupplierId(String supplierId, String materialCode);

    @Query("select s from SupplierStock s where s.supplier.supplierId =:supplierId")
    List<SupplierStock> findBySupplierId(String supplierId);

    Optional<SupplierStock> findBySupplier_SupplierNameAndMaterial_MaterialName(String supplierName, String materialName);

    @Query("select s from SupplierStock s where s.supplier.supplierName=:supplierName and s.material.materialCode=:materialCode")
    SupplierStock findBySupplierNameAndMaterialCode(String supplierName, String materialCode);

}
