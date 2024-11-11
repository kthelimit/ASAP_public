package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "InboundReceipt")
public class InboundReceipt  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_receipt_id")
    private Long inboundReceiptId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "item_code", nullable = false)
    private String itemCode;

    @Column(name = "specification", nullable = false)
    private String specification;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "confirmation", nullable = false)
    private Boolean confirmation;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "person_in_charge", nullable = false)
    private String personInCharge;

    @Column(name = "supplier_id", nullable = false)
    private String supplierId; // Assuming supplierId is a String
}
