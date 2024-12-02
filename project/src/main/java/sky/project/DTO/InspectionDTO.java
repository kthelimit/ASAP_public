package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionDTO {

    private Long inspectionId; // PK
    private Long orderId; // Order 테이블의 ID
    private String materialName; // 자재 이름
    private String supplierName; // 협력사 이름
    private Integer inspectionRound; // 검수 차수
    private Integer inspectionQuantity; // 검수 수량
    private Integer remainingQuantity; // 남은 검수 수량
    private String status; // 검수 상태
    private LocalDate inspectionDate; // 검수 요청 날짜
    private LocalDate expectedDelivery; // 예상 납품 날짜
}