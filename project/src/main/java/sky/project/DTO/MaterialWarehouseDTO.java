package sky.project.DTO;

import lombok.Data;

@Data
public class MaterialWarehouseDTO {
    private String warehouseId;
    private String itemName;
    private Integer stockQuantity;
    private Integer availableStock;
}
