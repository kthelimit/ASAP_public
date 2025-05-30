package sky.project.DTO;

import lombok.*;
import sky.project.Entity.CurrentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportDTO {
    private Long importId;              // 입고 ID (PK)
    private Long deliveryId;
    private String orderCode;          // 발주 번호
    private String importCode;
    private String materialCode;        // 자재 코드
    private String materialName;        // 품명
    private String materialType;
    private String supplierName;        // 업체명
    private String orderNumber;         // 발주 번호
    private LocalDate orderDate;           // 발주일
    private String expectedDate;        // 입고 예정일
    private String deliveryDate;        // 조달 납기일
    private int stock;                  // 재고
    private int orderedQuantity;
    private int importQuantity;        // 딜리버리 오더로받은수량
    private int quantity;               // 실제 납품 수량 (입고 수량)
    private int passedQuantity;         // 합격 수량
    private int defectiveQuantity;      // 결함 수량
    private CurrentStatus importStatus; // 현재 상태 (검수 전, 검수 완료 등)

    private LocalDateTime modifiedDate;
    private ReturnDTO returnDTO;

}
