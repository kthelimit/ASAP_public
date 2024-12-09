package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String orderCode;
    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate orderDate;

    @Column(name = "expected_date", nullable = false)
    private LocalDate expectedDate;

    private String procurePlanCode;

    @ManyToOne
    @JoinColumn(name = "supplier_name", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "material_name")
    private Material material;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    private double totalPrice;

    private String remarks; //비고
    @Enumerated(EnumType.STRING)
    private CurrentStatus status;

}
