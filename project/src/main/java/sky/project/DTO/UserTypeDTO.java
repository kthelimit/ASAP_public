package sky.project.DTO;

import lombok.Data;

@Data
public class UserTypeDTO {
    private Long userTypeId;
    private Boolean isAdmin;
    private String company;
    private Boolean isPurchaseDept;
    private Boolean isMaterialDept;
    private Boolean isProductionDept;
}