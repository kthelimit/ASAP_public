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
@Table(name = "inspection")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectionId; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Order 테이블과 연관 관계 (FK)

    @Column(nullable = false)
    private String materialName; // 자재 이름

    @Column(nullable = false)
    private String supplierName; // 협력사 이름

    @Column(nullable = false)
    private Integer inspectionRound; // 검수 차수 (1, 2, 3 등)

    @Column(nullable = false)
    private Integer inspectionQuantity; // 해당 차수에서 검수된 수량

    @Column(nullable = false)
    private Integer remainingQuantity; // 남은 검수 수량

    @Enumerated(EnumType.STRING)
    private CurrentStatus status; // 검수 상태 (진행 중, 완료 등)

    @Column(nullable = false)
    private LocalDate inspectionDate; // 검수 요청 날짜

    @Column(nullable = false)
    private LocalDate expectedDelivery; // 예상 납품 날짜
}
