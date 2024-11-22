package sky.project.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    private Long stockId; //내부 구분용 키
    
    private String materialType; //자재유형

    private String materialCode; //자재코드

    private String materialName; //자재 이름

    private String componentType; //부품 종류

    private Double unitPrice; //자재 가격

    private int quantity; //재고

    private int availableStock; //가용 재고
}
