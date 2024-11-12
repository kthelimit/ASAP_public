package sky.project.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "receipt")
@Getter
@Setter
public class Receipt extends Base {

    @Id
    @Column(name = "receipt_id", length = 20, nullable = false)
    private String receiptId;

    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "lot_no", length = 20)
    private String lotNo;

    @Column
    private String materialCode; // 자재 코드

    @Column
    private String materialName; // 자재 이름

    @Column(name = "specification", length = 50)
    private String specification;

    @Column(name = "unit", length = 10)
    private String unit;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "responsible", length = 100)
    private String responsible;
}
