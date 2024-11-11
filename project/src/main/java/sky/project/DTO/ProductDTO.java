package sky.project.DTO;

import lombok.Data;
import java.util.Date;

@Data
public class ProductDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private String description;
    private Double unitPrice;
    private String imageUrl;
    private Date createdDate;
}
