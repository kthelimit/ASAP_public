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
    private String supplierName; // 공급사 이름
    private String materialName; // 자재 이름
    private Double unitPrice; // 자재 단가
    private Integer quantity; // 거래 수량
    private Double totalPrice; // 총 가격 (unitPrice * quantity)
    private LocalDate createdDate; // 거래 발생 날짜
}
