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

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

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
