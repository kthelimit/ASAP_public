package sky.project.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProductionPlanDTO {

    private Long planId;
    private String productCode;
    private String productName;
    private LocalDate productionStartDate;
    private LocalDate productionEndDate;
    private Integer productionQuantity;
    private String productionPlanCode;
}
