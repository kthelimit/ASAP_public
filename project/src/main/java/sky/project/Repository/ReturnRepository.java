package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Returns;

public interface ReturnRepository extends JpaRepository<Returns, Long> {

    @Query("select r from Returns r where r.myImport.importId=:importId")
    Returns findByImportId(Long importId);
}
