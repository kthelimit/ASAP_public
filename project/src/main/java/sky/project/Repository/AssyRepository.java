package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Assy;
import sky.project.Entity.Material;

import java.util.List;

public interface AssyRepository extends JpaRepository<Assy, Long> {

    @Query("select a.assemblyMaterial from Assy a where a.product.productCode=:productCode")
    List<Material> findAssyMaterialByProductCode(String productCode);

    @Query("select a from Assy a where a.assemblyMaterial.materialCode=:assyMaterialCode")
    List<Assy> findByAssyMaterialCode(String assyMaterialCode);

    @Query("select a from Assy a where a.material.materialCode=:materialCode")
    List<Assy> findByMaterialCode(String materialCode);

    @Query("select count(a) from Assy  a where a.assemblyMaterial.materialCode=:assyMaterialCode")
    int findCountByAssyMaterialCode(String assyMaterialCode);

}
