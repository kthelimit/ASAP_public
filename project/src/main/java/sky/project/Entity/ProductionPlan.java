package sky.project.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "production_plan")
@Getter
@Setter
public class ProductionPlan extends Base {

    @Id
    @Column(name = "plan_id", length = 20, nullable = false)
    private String planId;

    @Column(name = "product_code", length = 20)
    private String productCode;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "production_start_date")
    private LocalDate productionStartDate;

    @Column(name = "production_end_date")
    private LocalDate productionEndDate;

    @Column(name = "production_quantity")
    private Integer productionQuantity;
}