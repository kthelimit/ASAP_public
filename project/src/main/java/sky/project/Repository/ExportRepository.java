package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select e from Export e where e.material.materialCode=:materialCode and e.exportStatus=0")
    List<Export> findByMaterialCode(String materialCode);

    @Query("select e from Export e where e.exportCode=:exportCode")
    Export findByExportCode(String exportCode);



    //검색용
    @Query("select e from Export e where e.exportCode like %:exportCode%")
    Page<Export> findByExportCode(String exportCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productionPlanCode like %:productionPlanCode%")
    Page<Export> findByProductionPlanCode(String productionPlanCode, Pageable pageable);

    @Query("select e from Export e where e.material.materialName like %:materialName%")
    Page<Export> findByMaterialName(String materialName, Pageable pageable);

    @Query("select e from Export e where e.material.materialCode like %:materialCode%")
    Page<Export> findByMaterialCode(String materialCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productName like %:productName%")
    Page<Export> findByProductName(String productName, Pageable pageable);
}
