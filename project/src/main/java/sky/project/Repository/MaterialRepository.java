package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
