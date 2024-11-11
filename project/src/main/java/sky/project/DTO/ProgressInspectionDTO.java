package sky.project.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProgressInspectionDTO {
    private Long orderId;
    private LocalDate orderDate;
    private LocalDate expectedArrivalDate;
    private String itemCode;
    private String itemName;
    private Integer orderQuantity;
    private String inspectionStatus;
    private Double inspectionRate;
    private Boolean inspectionResult;
}
