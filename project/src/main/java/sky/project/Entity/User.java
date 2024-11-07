package sky.project.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String userId;
    private String username;
    private String userAddress;
    private String userType = "PARTNER";
    private String password;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private LocalDate creationDate = LocalDate.now();
}