package sky.project.Service;

import sky.project.DTO.GraphDTO;
import sky.project.DTO.StockTrailDTO;
import sky.project.Entity.StockTrail;

import java.util.List;
import java.util.Map;

public interface StockTrailService {
    void register(StockTrail entity);

    List<StockTrailDTO> findAllStockTrails();

    Map<String, GraphDTO> getYearlyStockSummary();

    Map<String, GraphDTO> getMonthlyStockSummary();

    Map<String, GraphDTO> getWeeklyStockSummary();
}
