package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.StockDTO;
import sky.project.Entity.Stock;

import java.util.List;

public interface StockService {

    //등록
    Long register(StockDTO dto);

    //목록 불러오기
    Page<StockDTO> getStocks(Pageable pageable);

    List<Stock> getStocks();

    //검색 목록 불러오기
    Page<StockDTO> getStocksWithSearchInMaterialName(String keyword, Pageable pageable);

    Page<StockDTO> getStocksWithSearchInMaterialType(String keyword, Pageable pageable);

    Page<StockDTO> getStocksWithSearchInMaterialCode(String keyword, Pageable pageable);

    Page<StockDTO> getStocksWithSearchInComponentType(String keyword, Pageable pageable);

    int calculateAvailableStock(Stock stock);
}
