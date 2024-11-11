package sky.project.DTO;

import lombok.Data;

@Data
public class InspectionDTO {
    private String inspectionId;
    private String itemName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private Integer acceptedQuantity;
    private String orderId;
    private String status;
}
