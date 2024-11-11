package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Order")
public class Order {

    @Id
    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Column(name = "expected_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expectedDate;

    @Column(name = "item_code", length = 20, nullable = false)
    private String itemCode;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;
}
