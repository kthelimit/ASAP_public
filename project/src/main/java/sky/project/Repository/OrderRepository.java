package sky.project.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.project.DTO.OrdersDTO;
import sky.project.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findBySupplierNameContainingOrMaterialNameContaining(String supplierName, String materialName, Pageable pageable);
}

