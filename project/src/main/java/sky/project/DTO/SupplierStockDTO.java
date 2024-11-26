package sky.project.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierStockDTO {
    private Long supplierStockId; //업체 창고 자재 id
    
    private String supplierName; //업체 이름
    
    private String supplierId; //업체 id
    
    private String materialType; //자재유형

    private String materialCode; //자재코드

    private String materialName; //자재 이름
    
    private int quantity; //최소 공급 수량

    private String componentType; //부품 종류

    private Double unitPrice; //자재 가격

    private int stock; //재고

    private int availableStock; //가용 재고

}
