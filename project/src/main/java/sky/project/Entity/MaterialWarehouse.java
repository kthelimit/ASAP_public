package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MaterialWarehouse")
public class MaterialWarehouse  extends Base{

    @Id
    @Column(name = "warehouse_id", length = 20)
    private String warehouseId;


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드


    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;
}
