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
    private String procurePlanCode;
    private String productName;
    private String supplierName;
    private String materialName;
    private String materialCode;
    private int procurementQuantity;
    private LocalDate procurementDueDate;
    @Enumerated(EnumType.STRING)
    private CurrentStatus status;
}

