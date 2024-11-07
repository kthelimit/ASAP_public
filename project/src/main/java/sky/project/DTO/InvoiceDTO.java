package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private Long invoiceId; // 거래 명세서 ID
    private Long supplierId; // 공급사 ID
    private String supplierName; // 공급사 이름
    private Long materialId; // 자재 ID
    private String materialName; // 자재 이름
    private Double unitPrice; // 자재 단가
    private Integer quantity; // 거래 수량
    private Double totalPrice; // 총 가격 (unitPrice * quantity)
    private LocalDate transactionDate; // 거래 발생 날짜
    private String description; // 거래 설명 (선택 사항)
}
