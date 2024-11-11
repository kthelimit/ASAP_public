package sky.project.DTO;

import lombok.Data;

@Data
public class OutboundDTO {
    private Long outboundId;
    private String productionPlanId;
    private String productName;
    private String itemName;
    private String itemCode;
    private Integer stockQuantity;
    private Integer outboundQuantity;
}

