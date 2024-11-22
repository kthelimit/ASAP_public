package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "Assy")
public class Assy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assyId;

    @ManyToOne
    @JoinColumn(name="assymaterial_code")
    private Material assemblyMaterial; //조립 결과물

    @ManyToOne
    @JoinColumn(name="material_code")
    private Material material; //들어가는 자재

    @ManyToOne
    @JoinColumn(name="product_code")
    private Product product; //최종 상품

    private int quantity; // 들어가는 자재 수량
}
