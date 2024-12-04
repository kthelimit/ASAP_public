package sky.project.Service;

import sky.project.DTO.SupplierStockDTO;
import sky.project.Entity.SupplierStock;

import java.util.List;

public interface SupplierStockService {

    Long register(SupplierStockDTO dto);

    void updateStock(SupplierStockDTO dto);

    List<SupplierStock> getStocks();

    List<SupplierStockDTO> findBySupplierId(String supplierId);


}
