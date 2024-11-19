package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Bom;
public interface BomRepository extends JpaRepository<Bom, Long> {
}
