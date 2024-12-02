package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Inspection;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

}
