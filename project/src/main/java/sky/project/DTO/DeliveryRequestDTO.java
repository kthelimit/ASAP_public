package sky.project.DTO;

import lombok.*;
import sky.project.Entity.CurrentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequestDTO {
    private Long id;
    private String orderCode;
    private String supplierName;
    private String materialName;
    private int requestedQuantity;
    private LocalDateTime requestedAt;
    private String status;
}