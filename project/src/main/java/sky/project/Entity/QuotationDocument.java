package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "QuotationDocument")
public class QuotationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotation_doc_id")
    private Long quotationDocId;

    @ManyToOne
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "recipient", nullable = false, length = 50)
    private String recipient;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
}
