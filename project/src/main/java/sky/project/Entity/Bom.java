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
public class Bom{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long bomId;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="product_code", nullable=false)
private Product product;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="material_code" ,nullable=false)
private Material material;

private int requireQuantity;
}
