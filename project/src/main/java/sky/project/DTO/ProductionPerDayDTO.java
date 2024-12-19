package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPerDayDTO {
    private Long id;
    private int productionQuantity; //일당 생산량
    private String productionPlanCode; //생산계획 코드
    private LocalDate productionDate;
}
