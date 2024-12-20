package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Export;

import java.time.LocalDateTime;
import java.util.List;

public interface ExportRepository extends JpaRepository<Export, Long> {


    @Query("SELECT COUNT(e) FROM Export e WHERE e.exportCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("select e, s as availableQuantity from Export e left join Stock s on e.material.materialCode = s.material.materialCode " +
            "where e.exportStatus = 0")
    List<Object[]> findByExportStatusOnHold();


    @Query("select e, s as availableQuantity from Export e left join Stock s on e.material.materialCode = s.material.materialCode " +
            "where e.exportStatus = 0")
    Page<Object[]> findByExportStatusOnHold(Pageable pageable);


    @Query("select e from Export e where e.material.materialCode=:materialCode and e.exportStatus=0")
    List<Export> findByMaterialCodeAndStatusOnHold(String materialCode);

    @Query("select e from Export e where e.exportCode=:exportCode")
    Export findByExportCode(String exportCode);


    //승인 완료된 건 전부 띄우고, 종료된 것은 정해진 기간 내의 것만 출력한다.
    @Query("select e from Export e where (e.exportStatus =5 and e.modifiedDate <=:today and e.modifiedDate>=:start) or e.exportStatus=1")
    Page<Export> findByStatusNotFinished(Pageable pageable, LocalDateTime start , LocalDateTime today);


    //남은 갯수 계산용1
    @Query("select sum(e.assyQuantity) from Export e " +
            "where e.productionPlan.productionPlanCode=:productionPlanCode and e.assyMaterial.materialCode=:assyMaterialCode ")
    Integer findSumByProductionPlanCodeAndAssyMaterialCode(String productionPlanCode, String assyMaterialCode);

    //남은 갯수 계산용2
    @Query("select count(e.assyQuantity) from Export e " +
            "where e.productionPlan.productionPlanCode=:productionPlanCode and e.assyMaterial.materialCode=:assyMaterialCode ")
    Integer findCountByProductionPlanCodeAndAssyMaterialCode(String productionPlanCode, String assyMaterialCode);


    @Query("select count(e) from Export e " +
            "where e.productionPlan.productionPlanCode=:productionPlanCode and e.material.materialCode=:materialCode ")
    Integer findCountByProductionPlanCodeAndMaterialCode(String productionPlanCode, String materialCode);


    @Query("select sum(e.requiredQuantity) from Export e where e.productionPlan.productionPlanCode=:productionPlanCode "
            + "and e.material.materialCode=:materialCode")
    Integer findSumByProductionPlanCodeAndMaterialCode(String productionPlanCode, String materialCode);

    //검색용
    @Query("select e from Export e where e.exportCode like %:exportCode%")
    Page<Export> findByExportCode(String exportCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productionPlanCode like %:productionPlanCode%")
    Page<Export> findByProductionPlanCode(String productionPlanCode, Pageable pageable);

    @Query("select e from Export e where e.material.materialName like %:materialName%")
    Page<Export> findByMaterialName(String materialName, Pageable pageable);

    @Query("select e from Export e where e.material.materialCode like %:materialCode%")
    Page<Export> findByMaterialCodeAndStatusOnHold(String materialCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productName like %:productName%")
    Page<Export> findByProductName(String productName, Pageable pageable);

    //검색용2
    @Query("select e from Export e where e.exportCode like %:exportCode% " + "and e.exportStatus = 0")
    Page<Export> findByExportCodeOnHold(String exportCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productionPlanCode like %:productionPlanCode% " + "and e.exportStatus = 0")
    Page<Export> findByProductionPlanCodeOnHold(String productionPlanCode, Pageable pageable);

    @Query("select e from Export e where e.material.materialName like %:materialName% " + "and e.exportStatus = 0")
    Page<Export> findByMaterialNameOnHold(String materialName, Pageable pageable);

    @Query("select e from Export e where e.material.materialCode like %:materialCode% " + "and e.exportStatus = 0")
    Page<Export> findByMaterialCodeOnHold(String materialCode, Pageable pageable);

    @Query("select e from Export e where e.productionPlan.productName like %:productName% " + "and e.exportStatus = 0")
    Page<Export> findByProductNameOnHold(String productName, Pageable pageable);


    //대시 보드 출력용 출고 요청 건수
    @Query("select count(e) from Export e where e.exportStatus =0")
    int countCurrentRequest();

    //대시 보드 출력용 이번달 출고 승인 건수
    @Query("select count(e) from Export e where  e.exportStatus!=0 and e.createdDate>=:start and e.createdDate<=:end")
    int countApprovedRequest(LocalDateTime start, LocalDateTime end);

    //대시 보드 출력용 이번달 불출 완료 건수
    @Query("select count(e) from Export e where  e.exportStatus=5 and e.createdDate>=:start and e.createdDate<=:end")
    int countFinishedRequest(LocalDateTime start, LocalDateTime end);

    @Query("select e from Export e order by e.exportId desc limit 5")
    List<Export> findRecentExport();
}
