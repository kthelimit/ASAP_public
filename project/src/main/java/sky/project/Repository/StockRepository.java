package sky.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sky.project.Entity.Material;
import sky.project.Entity.Stock;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    //검색
    @Query("select s from Stock s where s.material.materialType like %:materialType%")
    Page<Stock> findByMaterialType(String materialType, Pageable pageable);

    @Query("select s from Stock s where s.material.materialName like  %:materialName%")
    Page<Stock> findByMaterialName(String materialName, Pageable pageable);

    @Query("select s from Stock s where s.material.materialCode like %:materialCode%")
    Page<Stock> findByMaterialCode(String materialCode, Pageable pageable);

    @Query("select s from Stock s where s.material.componentType like %:componentType%")
    Page<Stock> findByComponentType(String componentType, Pageable pageable);


    @Query("select s from Stock s where s.material.unitPrice=:unitPrice")
    Stock findByUnitPrice(double unitPrice);

    //자재 코드로 창고 자재목록 찾아오기
    @Query("select s from Stock s where s.material.materialCode=:materialCode")
    Stock findByMaterialCode(String materialCode);



//    //창고 자재 목록을 가용재고와 같이 불러오기
//    @Query("select s, e as availableQuantity from Stock s left join Export e on s.material.materialCode = e.material.materialCode where e.exportStatus=0")
//    List<Object[]> findAllWithAvailableQuantity();
}
