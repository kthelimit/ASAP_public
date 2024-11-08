package sky.project.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    private String supplierId; // 공급사 ID (User ID와 동일하게 설정됨)
    private String userId; // 사용자 ID (User 엔티티와의 관계)
    private String businessRegistrationNumber; // 사업자 등록 번호
    private String supplierName; // 공급사 이름 (회사명)
    private String contactInfo; // 연락처 정보
    private String address; // 공급사 주소
    private String businessType; // 사업 유형 (예: 제조업, 유통업)
    private Boolean approved; // 승인 여부
}
