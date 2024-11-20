package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId; // 자재 ID

    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드

    @Column(nullable = false)
    private String materialType; // 자재 유형 (대부품/소부품)

    private int width;

    private int height;

    private int depth;

    private String componentType;

    @Column(nullable = false)
    private Double unitPrice; // 단위당 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier; // 공급사 정보와의 관계

    @Column(nullable = false)
    private Integer quantity; // 최소 공급 수량

    @Column(nullable = false)
    private String imageUrl; // 자재 이미지 URL

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    private int leadtime;
}
