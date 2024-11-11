package sky.project.DTO;

import lombok.Data;
import java.util.Date;

@Data
public class InboundReceiptDTO {
    private Long inboundReceiptId;
    private String orderId;
    private String itemName;
    private String lotNumber;
    private String itemCode;
    private String specification;
    private String unit;
    private Integer quantity;
    private Boolean confirmation;
    private Date date;
    private String personInCharge;
    private String supplierId;
}

