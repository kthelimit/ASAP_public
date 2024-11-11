package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Shipment")
public class Shipment  extends Base{

    @Id
    @Column(name = "shipment_id", length = 20)
    private String shipmentId;

    @Column(name = "production_plan_id", length = 20, nullable = false)
    private String productionPlanId;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "item_code", length = 20, nullable = false)
    private String itemCode;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "shipment_quantity", nullable = false)
    private Integer shipmentQuantity;
}

