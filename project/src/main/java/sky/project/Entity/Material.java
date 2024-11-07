package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId; // 자재 ID

    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름

    @Column(nullable = false, unique = true, length = 50)
    private String materialCode; // 자재 코드

    @Column(nullable = false)
    private String materialType; // 자재 유형 (대부품/소부품)

    @Column(nullable = false, length = 20)
    private String unit; // 자재 단위 (e.g., pcs, kg)

    @Column(nullable = false)
    private Double unitPrice; // 단위당 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier; // 공급사 정보와의 관계

    @Column(nullable = false)
    private Integer quantity; // 최소 공급 수량

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt; // 등록 시간
}
