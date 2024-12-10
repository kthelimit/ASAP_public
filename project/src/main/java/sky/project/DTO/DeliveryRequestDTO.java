package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequestDTO {
    private Long id;
    private String deliveryCode;
    private Long orderId;
    private String orderCode; //발주 코드
    private String supplierName;
    private String materialName;
    private int requestedQuantity; //납품 지시를 요청한 수량
    private LocalDate requestedDate; //배달 받고 싶은 날짜
    private String status;
    private int availableStock; //상대의 가용 재고 수량
    private int totalOrderQuantity; //발주서에 기입된 총 수량
    private int remainingQuantity; //이것을 수행할 당시의 남은 조달 수량
    private int remainingQuantityAfter; //이것을 수행하고 난 후의 조달 수량
}