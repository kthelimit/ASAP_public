package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ProgressInspection")
public class ProgressInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_arrival_date", nullable = false)
    private LocalDate expectedArrivalDate;

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @Column(name = "inspection_status", length = 50)
    private String inspectionStatus;

    @Column(name = "inspection_rate")
    private Double inspectionRate;

    @Column(name = "inspection_result")
    private Boolean inspectionResult;
}
