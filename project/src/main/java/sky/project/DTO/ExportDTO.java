package sky.project.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportDTO {
    private Long exportId;
    private String exportCode; // 입고 코드
    private String productionPlanCode; //생산계획코드
    private String materialCode; //자재
    private int quantity;
    private String status;
}
