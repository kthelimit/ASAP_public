package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ProcurementPlan")
public class ProcurementPlan  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    private String productionPlanCode;
    private String productCode;
    private String productName;
    private String supplierName;
    private String materialName;
    private String materialCode;


    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "procurement_interval", nullable = false)
    private Integer procurementInterval;

    @Column(name = "procurement_quantity", nullable = false)
    private Integer procurementQuantity;

    @Column(name = "procurement_due_date", nullable = false)
    private LocalDate procurementDueDate;
}

