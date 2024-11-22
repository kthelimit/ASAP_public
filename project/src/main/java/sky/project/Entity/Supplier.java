package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends Base{

    @Id
    private String supplierId; // User ID에서 받아오는 공급사 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계

    @Column(nullable = false, unique = true, length = 20)
    private String businessRegistrationNumber;

    @Column(nullable = false, length = 100)
    private String supplierName;

    @Column(nullable = false, length = 100)
    private String contactInfo;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 50)
    private String businessType;

    @Column(nullable = false)
    private String businessItem;

    // Material과의 관계 추가
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Material> materials; // 공급사와 연결된 자재 목록

    @Column(nullable = false)
    private Boolean approved = false; // 기본값은 false

    @Column(name = "contract_file_path")
    private String contractFilePath; // 계약서 파일 경로
}
