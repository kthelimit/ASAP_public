package sky.project.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ReceiptDTO {

    private String receiptId;
    private String orderId;
    private String lotNo;
    private String materialName; // 자재 이름
    private String materialCode; // 자재 코드
    private String specification;
    private String unit;
    private Integer quantity;
    private Boolean verified;
    private LocalDate date;
    private String responsible;
}
