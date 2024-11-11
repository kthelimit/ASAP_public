package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId; // 거래 명세서 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier; // 공급사 정보 (Supplier 엔터티와의 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material; // 자재 정보 (Material 엔터티와의 관계)

    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름 (Material에서 가져온 이름)

    @Column(nullable = false)
    private Double unitPrice; // 자재 단가 (Material에서 가져온 가격)

    @Column(nullable = false)
    private Integer quantity; // 거래 수량

    @Column(nullable = false)
    private Double totalPrice; // 총 가격 (unitPrice * quantity)

    @Column(nullable = false)
    private LocalDate transactionDate; // 거래 발생 날짜

    @Column(length = 255)
    private String description; // 거래 설명 (선택 사항)
}
