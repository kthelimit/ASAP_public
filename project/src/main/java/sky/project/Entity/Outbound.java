package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Outbound")
public class Outbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbound_id")
    private Long outboundId;

    @Column(name = "production_plan_id", nullable = false)
    private String productionPlanId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_code", nullable = false)
    private String itemCode;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "outbound_quantity", nullable = false)
    private Integer outboundQuantity;
}

