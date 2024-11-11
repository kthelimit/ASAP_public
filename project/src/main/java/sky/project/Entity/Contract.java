package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "specification", nullable = false)
    private String specification;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "supply_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal supplyAmount;

    @Column(name = "tax", precision = 10, scale = 2, nullable = false)
    private BigDecimal tax;

    @Column(name = "sign_party_a", nullable = false)
    private String signPartyA;

    @Column(name = "sign_party_b", nullable = false)
    private String signPartyB;

    @Column(name = "contract_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date contractDate;
}
