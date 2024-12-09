package sky.project.DTO;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReturnDTO {
    private Long returnId;
    private Long importId;
    private int quantity;
    private String status;
}
