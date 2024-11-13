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


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드


    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "shipment_quantity", nullable = false)
    private Integer shipmentQuantity;
}

