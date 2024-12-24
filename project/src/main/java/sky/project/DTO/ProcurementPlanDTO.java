package sky.project.DTO;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProcurementPlanDTO {

    private Long planId; // 식별자
    private String productionPlanCode; // 생산 계획 코드
    private String procurePlanCode; // 조달 계획 코드
    private String productCode; // 제품 코드
    private String productName; // 제품 이름
    private String supplierName; // 공급업체 이름
    private String materialName; // 자재 이름
    private String materialCode; // 자재 코드
    private int procurementQuantity; // 조달 수량
    private LocalDate procurementDueDate; // 조달 완료 예정일
    private LocalDateTime createdDate;
    private String status; //조달계획 상태
    private int requireQuantity; // 추가된 필드
    private double unitPrice;


}

