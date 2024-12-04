package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Inspection;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    @Query("select count(i) from Inspection i where i.order.orderCode=:orderCode")
    int countByOrderCode(String orderCode);

    @Query("select i from Inspection i where i.order.orderCode=:orderCode")
    List<Inspection> findByOrderCode(String orderCode);

}
