package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import sky.project.DTO.StockDTO;

public interface StockService {

    //등록
    Long register(StockDTO dto);

    //목록 불러오기
    Page<StockDTO> getStocks(PageRequest pageRequest);
}
