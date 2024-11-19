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

@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
@JoinColumn(name="product_code", nullable=false)
private Product product;

private String componentType;

private String materialName;

private int requireQuantity;
}
