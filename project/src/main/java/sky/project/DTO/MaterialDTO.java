package sky.project.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
    private String supplierId; // 공급사 ID (Supplier 엔티티에서 가져옴)
    private String supplierName; // 공급사 이름 (회사명)
    private Integer quantity; // 최소 공급 수량
    private int width;
    private int height;
    private int depth;
    private String imageUrl; // 이미지 URL
    private MultipartFile imageFile; // 이미지 파일 업로드를 위한 필드 추가
    private BigDecimal weight;

}
