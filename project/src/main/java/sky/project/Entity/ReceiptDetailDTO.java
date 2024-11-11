package sky.project.Entity;

import lombok.Data;

import java.util.Date;

@Data
public class ReceiptDetailDTO {
    private String receiptId;
    private String orderId;
    private String lotNo;
    private String itemCode;
    private String specification;
    private String unit;
    private Integer quantity;
    private Boolean verified;
    private Date date;
    private String responsible;
}

