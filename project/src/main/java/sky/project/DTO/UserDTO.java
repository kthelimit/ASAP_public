package sky.project.DTO;

import lombok.*;
import sky.project.Entity.User;
import sky.project.Entity.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String username;
    private String userAddress;
    private UserType userType= UserType.PARTNER;
    private String password;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // User를 받아서 초기화하는 생성자 추가
    public UserDTO(User user) {
        this.userId = user.getUserId(); // User 엔티티의 ID 가져오기
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
    }
}
