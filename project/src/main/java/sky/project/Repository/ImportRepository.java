package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Import;

import java.util.List;

@Repository
public interface ImportRepository extends JpaRepository<Import, Long> {
    Page<Import> findByMaterialNameContaining(String keyword, Pageable pageable);
    Page<Import> findBySupplierNameContaining(String keyword, Pageable pageable);
    Page<Import> findByOrderCodeContaining(String keyword, Pageable pageable);

    @Query("select count(i) from Import i where i.importStatus='ON_HOLD'")
    int countImportOnHold();
    @Query("select count(i) from Import i where i.importStatus='UNDER_INSPECTION'")
    int countImportUnderInspection();

    @Query("select i from Import i order by i.importId desc limit 5")
    List<Import> findRecentImport();
}