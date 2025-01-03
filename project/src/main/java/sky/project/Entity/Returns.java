package sky.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Returns extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;
    private String returnCode;

    @ManyToOne
    @JoinColumn(name = "import_id", nullable = false)
    private Import myImport;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private CurrentStatus status;
}
