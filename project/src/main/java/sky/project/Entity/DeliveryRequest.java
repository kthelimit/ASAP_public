package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "deliveryrequest")
public class DeliveryRequest extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deliveryCode;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "supplier_name", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    private int requestedQuantity; //납품 지시를 요청한 수량

    private LocalDate requestedDate; //배달 받고 싶은 날짜

    @Enumerated(EnumType.STRING)
    private CurrentStatus status;


}
