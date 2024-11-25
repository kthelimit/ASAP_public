package sky.project.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportDTO {
    private Long exportId;
    private String exportCode; // 출고 코드
    private String productionPlanCode; //생산계획코드
    private String productName;
    private String materialCode; //자재
    private String materialName;
    private int requiredQuantity;
    private int availableQuantity;
    private String exportStatus;
    private LocalDateTime createdDate;
}
