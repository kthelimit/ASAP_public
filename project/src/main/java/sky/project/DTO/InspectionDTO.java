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
    private String inspectionCode;
    private String orderCode; // 발주코드
    private String materialName; // 자재 이름
    private String materialCode;
    private String supplierName; // 협력사 이름
    private Integer inspectionRound; // 검수 차수
    private Integer inspectionQuantity; // 검수 수량
    private Integer remainingQuantity; // 남은 검수 수량
    private String status; // 검수 상태
    private LocalDate inspectionDate; // 검수 요청 날짜
}