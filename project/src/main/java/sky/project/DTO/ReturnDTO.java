package sky.project.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReturnDTO {
    private Long returnId;
    private String returnCode;
    private String importCode;
    private Long importId;
    private String materialName;
    private int quantity;
    private LocalDateTime createdDate;
    private String status;
}
