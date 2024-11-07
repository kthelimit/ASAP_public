package sky.project.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDTO {

    private Long materialId; // 자재 ID
    private String materialName; // 자재 이름
    private String materialCode; // 자재 코드
    private String materialType; // 자재 유형 (대부품/소부품)
    private String unit; // 자재 단위 (e.g., pcs, kg)
    private Double unitPrice; // 단위당 가격
    private Long supplierId; // 공급사 ID
    private String supplierName; // 공급사 이름
    private Integer quantity; // 최소 공급 수량
    private String imageUrl;
    private LocalDateTime createdAt; // 등록 시간
}
