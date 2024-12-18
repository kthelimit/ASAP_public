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
public class StockTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_code", nullable = false)
    private Material material;

    private int quantity;

    private int stock;
    private double price;

    private LocalDateTime date;
}
