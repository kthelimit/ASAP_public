package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ProgressInspection")
public class ProgressInspection  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_arrival_date", nullable = false)
    private LocalDate expectedArrivalDate;


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드


    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @Column(name = "inspection_status", length = 50)
    private String inspectionStatus;

    @Column(name = "inspection_rate")
    private Double inspectionRate;

    @Column(name = "inspection_result")
    private Boolean inspectionResult;
}
