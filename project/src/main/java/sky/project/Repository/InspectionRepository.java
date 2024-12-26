package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Inspection;

import java.time.LocalDate;
import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    @Query("select count(i) from Inspection i where i.order.orderCode=:orderCode")
    int countByOrderCode(String orderCode);

    @Query("select count(i) from Inspection i where i.order.orderCode=:orderCode and i.status='FINISHED'")
    int countByOrderCodeWithFinished(String orderCode);

    @Query("select count(i) from Inspection i where i.supplier.supplierName=:supplierName and i.status='ON_HOLD'")
    int countBySupplierOnHold(String supplierName);

    //대시보드용 오늘이 진척 검수일인지 확인용
    @Query("select i from Inspection i where i.inspectionDate =:today and i.status='ON_HOLD'")
    List<Inspection> findInspectionsByInspectionDate(LocalDate today);

    //이전 차수 검수가 끝났으면 그 종료 수량을 가져와야하기때문에 리스트를 가져온다.
    @Query("select i from Inspection i where i.order.orderCode=:orderCode " +
            "and i.status='FINISHED' and i.inspectionRound<:inspectionRound")
    List<Inspection> countSumQuantityFromLastFinishedInspection(String orderCode, int inspectionRound);

    @Query("select i from Inspection i where i.order.orderCode=:orderCode")
    List<Inspection> findByOrderCode(String orderCode);

    //협력사 페이지에 띄워주는 진척검수 목록
    @Query("select i from Inspection i where i.supplier.supplierName=:supplierName and (i.status='ON_HOLD' or (i.status='FINISHED' and i.inspectionDate >:limitDate)) " +
            "order by i.status desc ")
    Page<Inspection> findBySupplierName(String supplierName, LocalDate limitDate, Pageable pageable);
}
