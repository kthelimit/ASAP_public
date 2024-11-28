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

    private String orderId; // 입고 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_code", nullable = false)
    private Material material; //자재

    private String supplierName;

    private int orderedQuantity; // 실제 납품 수량

    private int quantity; //수량

    private int passedQuantity; // 합격 수량

    private CurrentStatus importStatus; //현재 상태

}
