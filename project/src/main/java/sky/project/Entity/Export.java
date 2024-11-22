package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "Export")
public class Export {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exportId;

    private String exportCode; // 입고 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionPlan_code", nullable = false)
    private ProductionPlan productionPlan; //생산계획

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_code", nullable = false)
    private Material material; //자재

    private int requiredQuantity; //요구 수량

    private CurrentStatus exportStatus; //현재 상태

}
