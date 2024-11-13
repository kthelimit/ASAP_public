package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order  extends Base{

    @Id
    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Column(name = "expected_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expectedDate;


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;
}
