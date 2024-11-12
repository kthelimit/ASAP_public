package sky.project.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class OrdersDTO {

    private String orderId;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private String materialName; // 자재 이름
    private String materialCode; // 자재 코드
    private Integer orderQuantity;
}
