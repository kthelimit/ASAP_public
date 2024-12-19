package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private boolean isRegister = false;

    @Builder.Default
    private List<SupplierDTO> suppliers = new ArrayList<>();

    @Builder.Default
    private List<MaterialDTO> Materials = new ArrayList<>();

    //가용재고 불러오기용
    private int availableStock;

    //남은 조달 수량 불러오기용
    private int remainedOrderQuantity;

    //조달 납기일 불러오기용
    private LocalDate dayAfterLeadTime;

}
