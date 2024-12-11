package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Returns;

import java.util.List;

public interface ReturnRepository extends JpaRepository<Returns, Long> {

    @Query("select r from Returns r where r.myImport.importId=:importId")
    Returns findByImportId(Long importId);

    @Query("select r from Returns r where r.myImport.supplierName=:supplierName and r.status='ON_HOLD'")
    List<Returns> findBySupplierNameOnHOLD(String supplierName);

    @Query("select count(r) from Returns r where r.myImport.supplierName=:supplierName and r.status='ON_HOLD'")
    int countBySupplierNameOnHOLD(String supplierName);

    @Query("SELECT COUNT(r) FROM Returns r WHERE r.returnCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

}
