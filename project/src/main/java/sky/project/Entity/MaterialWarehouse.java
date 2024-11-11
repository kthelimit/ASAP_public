package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MaterialWarehouse")
public class MaterialWarehouse {

    @Id
    @Column(name = "warehouse_id", length = 20)
    private String warehouseId;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;
}
