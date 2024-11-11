package sky.project.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuotationDTO {
    private Long quotationId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
}

