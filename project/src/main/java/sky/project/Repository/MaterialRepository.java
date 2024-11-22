package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Material;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByMaterialNameAndMaterialCode(String materialName, String materialCode);

    @Query("SELECT m from Material m where m.componentType = :componentType")
    List<Material> findByComponentType(String componentType);

    @Query("select m from Material m where m.materialType =:materialType")
    List<Material> findByMaterialType(String materialType);

    Optional<Material> findByMaterialCode(String materialCode);
}
