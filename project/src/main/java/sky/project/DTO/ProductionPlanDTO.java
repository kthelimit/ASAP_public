package sky.project.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionPlanDTO {

    private Long planId;
    private String productCode;
    private String productName;
    private LocalDate productionStartDate;
    private LocalDate productionEndDate;
    private Integer productionQuantity;
    private String productionPlanCode;
}
