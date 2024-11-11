package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Quotation")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotation_id")
    private Long quotationId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
}
