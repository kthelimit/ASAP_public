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
public class Export extends Base{
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assy_material_code", nullable = false)
    private Material assyMaterial;//결과가 되는 조립자재

    private int assyQuantity; //결과 자재가 나오는 수량

    private int requiredQuantity; //요구 수량

    private CurrentStatus exportStatus; //현재 상태

}
