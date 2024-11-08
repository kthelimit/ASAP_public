package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String supplierId; // User ID에서 받아오는 공급사 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계

    @Column(nullable = false, unique = true, length = 20)
    private String businessRegistrationNumber; // 사업자 등록 번호

    @Column(nullable = false, length = 100)
    private String supplierName; // 공급사 이름 (회사명)

    @Column(nullable = false, length = 100)
    private String contactInfo; // 연락처 정보

    @Column(nullable = false, length = 255)
    private String address; // 공급사 주소

    @Column(nullable = false, length = 50)
    private String businessType; // 사업 유형 (예: 제조업, 유통업)

    @Column(nullable = false)
    private Boolean approved = false; // 승인 여부 (기본값은 승인되지 않은 상태)
}
