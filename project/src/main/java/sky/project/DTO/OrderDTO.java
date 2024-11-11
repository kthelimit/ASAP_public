package sky.project.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class OrderDTO {
    private String orderId;
    private Date orderDate;
    private Date expectedDate;
    private String itemCode;
    private String itemName;
    private Integer orderQuantity;
}
