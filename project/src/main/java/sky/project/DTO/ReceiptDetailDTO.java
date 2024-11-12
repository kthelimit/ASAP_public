package sky.project.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ReceiptDetailDTO {
    private String receiptId;
    private String orderId;
    private String lotNo;
    private String materialName; // 자재 이름
    private String materialCode; // 자재 코드
    private String specification;
    private String unit;
    private Integer quantity;
    private Boolean verified;
    private Date date;
    private String responsible;
}

