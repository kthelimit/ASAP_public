package sky.project.DTO;

import lombok.*;
import sky.project.Entity.UserType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String username;
    private String userAddress;
    private UserType userType = UserType.PARTNER;
    private String password;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private LocalDate creationDate = LocalDate.now();
}
