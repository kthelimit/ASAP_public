package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "Import")
public class Import extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long importId;

    private String orderCode; // 발주 번호

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private DeliveryRequest deliveryRequest;

    private String importCode;

    private String materialName; // 자재이름

    private String supplierName;

    private int orderedQuantity; // 실제 납품 수량

    private int quantity; //수량

    private int passedQuantity; // 합격 수량

    private int defectiveQuantity;      // 결함 수량

    @Enumerated(EnumType.STRING)
    private CurrentStatus importStatus; //현재 상태

}
