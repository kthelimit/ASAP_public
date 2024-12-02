package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;


@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate orderDate;

    @Column(name = "expected_date", nullable = false)
    private LocalDate expectedDate;

    private String procurePlanCode;
    private String supplierName;


    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    private double totalprice;


    @Enumerated(EnumType.STRING)
    private CurrentStatus status;

    @Column(name = "required_quantity", nullable = true) // 조달 필요 수량
    private Integer requiredQuantity;

    @Column(name = "available_stock", nullable = true) // 요청 가능 수량
    private Integer availableStock;

}
