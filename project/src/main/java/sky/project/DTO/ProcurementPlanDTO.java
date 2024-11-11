package sky.project.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProcurementPlanDTO {
    private Long planId;
    private LocalDate productionStartDate;
    private LocalDate productionEndDate;
    private String productCode;
    private String productName;
    private Integer productionQuantity;
    private String itemCode;
    private String itemName;
    private Integer requiredQuantity;
    private Integer procurementInterval;
    private Integer procurementQuantity;
    private LocalDate procurementDueDate;
}
