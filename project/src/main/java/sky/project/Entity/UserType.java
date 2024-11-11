package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "UserType")
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Long userTypeId;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "is_purchase_dept", nullable = false)
    private Boolean isPurchaseDept;

    @Column(name = "is_material_dept", nullable = false)
    private Boolean isMaterialDept;

    @Column(name = "is_production_dept", nullable = false)
    private Boolean isProductionDept;
}
