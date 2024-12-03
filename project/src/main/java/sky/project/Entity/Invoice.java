package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice  extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId; // 거래 명세서 ID

   private String supplierName;

    @Column(nullable = false, length = 100)
    private String materialName; // 자재 이름 (Material에서 가져온 이름)

    @Column(nullable = false)
    private Double unitPrice; // 자재 단가 (Material에서 가져온 가격)

    @Column(nullable = false)
    private Integer quantity; // 거래 수량

    @Column(nullable = true)
    private Double totalPrice; // 총 가격 (unitPrice * quantity)

}
