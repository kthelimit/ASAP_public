package sky.project.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Order;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);


    //대시보드 출력용 이번달 발주 건수
    @Query("select count(o) from Order o where o.createdDate>=:start and o.createdDate<=:end")
    int countOrderThisMonth(LocalDateTime start, LocalDateTime end);
}

