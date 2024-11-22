package sky.project.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomDTO {
    private Long bomId; // Bom ID
    private String productCode; // 제품 코드
    private String componentType; // 부품 타입
    private String materialCode; // 자재 코드
    private String materialName; // 자재 이름
    private int requireQuantity; // 필요 수량
    private List<MaterialDTO> suppliers;
    private int availableStock;
    private int procurementQuantity;
}
