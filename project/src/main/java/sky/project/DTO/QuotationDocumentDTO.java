package sky.project.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class QuotationDocumentDTO {
    private Long quotationDocId;
    private Long quotationId;
    private LocalDate date;
    private String recipient;
    private Long supplierId;
    private BigDecimal totalAmount;
}

