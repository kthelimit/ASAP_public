package sky.project.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="deliveryrequest")
public class DeliveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String supplierName;

    private String materialName;

    private int requestedQuantity;

    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private CurrentStatus status;
}
