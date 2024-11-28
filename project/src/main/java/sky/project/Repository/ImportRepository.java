package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Import;

@Repository
public interface ImportRepository extends JpaRepository<Import, Long> {

}