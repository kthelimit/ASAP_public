package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Inspection")
public class Inspection  extends Base{

    @Id
    @Column(name = "inspection_id", length = 20)
    private String inspectionId;

    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드


    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;

    @Column(name = "accepted_quantity", nullable = false)
    private Integer acceptedQuantity;

    @Column(name = "order_id", length = 20, nullable = false)
    private String orderId;

    @Column(name = "status", length = 50)
    private String status;
}
