package sky.project.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractDTO {
    private Long contractId;
    private String item;
    private String specification;
    private BigDecimal unitPrice;
    private BigDecimal supplyAmount;
    private BigDecimal tax;
    private String signPartyA;
    private String signPartyB;
    private Date contractDate;
}

