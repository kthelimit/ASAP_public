package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Export;

import java.util.List;

public interface ExportRepository extends JpaRepository<Export, Long> {


    @Query("SELECT COUNT(e) FROM Export e WHERE e.exportCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("select e, s as availableQuantity from Export e left join Stock s on e.material.materialCode = s.material.materialCode " +
            "where e.exportStatus = 0")
    List<Object[]> findByExportStatusOnHold();
}
