package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.project.Entity.Import;

@Repository
public interface ImportRepository extends JpaRepository<Import, Long> {
    Page<Import> findByMaterialNameContaining(String keyword, Pageable pageable);
    Page<Import> findBySupplierNameContaining(String keyword, Pageable pageable);
    Page<Import> findByOrderCodeContaining(String keyword, Pageable pageable);


}