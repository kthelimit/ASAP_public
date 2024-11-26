package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierStockId;

    @ManyToOne
    @JoinColumn(name = "materialCode")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    private int availableStock; //가용 재고
}
