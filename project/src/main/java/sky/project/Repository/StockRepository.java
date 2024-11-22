package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("select s from Stock s where s.material.materialType like %:materialType%")
    Page<Stock> findByMaterialType(String materialType, Pageable pageable);

    @Query("select s from Stock s where s.material.materialName like  %:materialName%")
    Page<Stock> findByMaterialName(String materialName, Pageable pageable);

    @Query("select s from Stock s where s.material.materialCode like %:materialCode%")
    Page<Stock> findByMaterialCode(String materialCode, Pageable pageable);

    @Query("select s from Stock s where s.material.componentType like %:componentType%")
    Page<Stock> findByComponentType(String componentType, Pageable pageable);

    @Query("select s from Stock s where s.material.materialCode=:materialCode")
    Stock findByMaterialCode(String materialCode);


}
