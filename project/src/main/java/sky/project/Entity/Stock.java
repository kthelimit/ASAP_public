package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId; //내부 구분용 키

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "materialCode")
    private Material material; //자재

    private int quantity; //재고

    private int availableStock; //가용 재고


}
