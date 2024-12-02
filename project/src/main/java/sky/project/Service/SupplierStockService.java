package sky.project.Service;

import sky.project.DTO.SupplierStockDTO;

import java.util.List;

public interface SupplierStockService {

    Long register(SupplierStockDTO dto);

    void updateStock(SupplierStockDTO dto);

    List<SupplierStockDTO> findBySupplierId(String supplierId);


}
