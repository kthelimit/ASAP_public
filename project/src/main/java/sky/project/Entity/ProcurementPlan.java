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

    @Column(name = "production_start_date", nullable = false)
    private LocalDate productionStartDate;

    @Column(name = "production_end_date", nullable = false)
    private LocalDate productionEndDate;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "production_quantity", nullable = false)
    private Integer productionQuantity;


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드


    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "procurement_interval", nullable = false)
    private Integer procurementInterval;

    @Column(name = "procurement_quantity", nullable = false)
    private Integer procurementQuantity;

    @Column(name = "procurement_due_date", nullable = false)
    private LocalDate procurementDueDate;
}

