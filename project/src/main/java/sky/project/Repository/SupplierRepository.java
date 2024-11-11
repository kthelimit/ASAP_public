package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.Supplier;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, String> {
    boolean existsByUserUserId(String userId);

    Page<Supplier> findByApprovedFalse(PageRequest pageable);
    Optional<Supplier> findByUser_UserId(String userId);
    long countByApprovedFalse();

}
