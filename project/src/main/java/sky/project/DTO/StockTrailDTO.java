package sky.project.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTrailDTO {
    private int id;
    private String materialCode; // 자재 코드 (Material의 식별자)
    private String materialName; // 자재 이름 (추가로 가져오고 싶은 경우)
    private int quantity;        // 입출고 수량
    private int stock;           // 재고 수량
    private double price;           //
    private LocalDateTime date;  // 작업 날짜
}
