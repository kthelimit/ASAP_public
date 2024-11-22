package sky.project.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssyDTO {
    private Long assyId;

    private String assyMaterialName; //조립 결과물의 자재명
    private String assyMaterialCode; //조립 결과물의 자재코드

    private String componentType; //재료로 들어가는 자재의 부품종류
    private String materialName; //재료로 들어가는 자재명
    private String materialCode; //재료로 들어가는 자재코드
    private int quantity; //들어가는 자재 수량
    
    private String productCode; //최종 상품코드
    private String productName; //최종 상품명


}
