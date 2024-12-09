package sky.project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int returnId;

    @ManyToOne
    @JoinColumn(name = "import_id", nullable = false)
    private Import myImport;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private CurrentStatus status;
}
