package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Material;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m from Material m where m.componentType = :componentType")
    List<Material> findByComponentType(String componentType);

    @Query("select m from Material m where m.materialType =:materialType")
    List<Material> findByMaterialType(String materialType);



    Optional<Material> findByMaterialCode(String materialCode);

    Page<Material> findByMaterialNameContainingOrMaterialCodeContaining(
            String materialName, String materialCode, Pageable pageable);

    List<Material> findByMaterialName(String materialName);

    Optional<Material> findFirstByMaterialName(String materialName);


}
