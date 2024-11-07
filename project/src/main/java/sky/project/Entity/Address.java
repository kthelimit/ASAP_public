package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 필요에 따라 추가
    @Column(length = 10)
    private String postalCode;
    @Column(length = 50)
    private String province;
    @Column(length = 50)
    private String provinceEnglish;
    @Column(length = 50)
    private String cityDistrict;
    @Column(length = 50)
    private String cityDistrictEnglish;
    @Column(length = 50)
    private String town;

    @Column(length = 50)
    private String townEnglish;

    @Column(length = 20)
    private String roadCode;

    @Column(length = 100)
    private String roadName;

    @Column(length = 100)
    private String roadNameEnglish;

    private Integer belowGround;

    private Integer mainBuildingNumber;

    private Integer subBuildingNumber;

    @Column(length = 50)
    private String buildingManagementNumber;

    @Column(length = 100)
    private String bulkDeliveryName;

    @Column(length = 100)
    private String cityDistrictBuildingName;

    @Column(length = 20)
    private String legalDongCode;

    @Column(length = 100)
    private String legalDongName;

    @Column(length = 50)
    private String riName;

    @Column(length = 50)
    private String administrativeDongName;

    private Integer mountainStatus;

    private Integer landNumberMain;

    private Integer townSequenceNumber;

    private Integer landNumberSub;

    @Column(length = 10)
    private String oldPostalCode;

    @Column(length = 50)
    private String postalCodeSequenceNumber;

}
