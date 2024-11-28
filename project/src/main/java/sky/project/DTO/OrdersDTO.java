package sky.project.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDTO {
    private Long orderId;           // 주문 ID
    private String orderCode;       //발주서 코드
    private LocalDate orderDate;      // 발주일
    private LocalDate expectedDate;   // 입고 예정일
    private String procurePlanCode;   // 조달 계획 코드
    private String supplierName;      // 공급업체 이름
    private String materialName;      // 자재 이름
    private Integer orderQuantity;    // 주문 수량
    private double totalPrice;        // 총 가격
    private String status;
    private String materialType;
}
