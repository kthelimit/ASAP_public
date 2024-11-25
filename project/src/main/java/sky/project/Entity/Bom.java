package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "Bom")
public class Bom extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code", nullable = false)
    private Product product;

    private String componentType;

    //private String materialName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_code")
    private Material material;

    private int requireQuantity;
}
