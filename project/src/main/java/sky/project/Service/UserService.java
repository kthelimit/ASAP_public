package sky.project.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.project.Entity.UserType;
import sky.project.DTO.UserDTO;
import sky.project.Entity.UserType;

import java.util.Optional;

public interface UserService {
    UserDTO authenticate(String userId, String password, UserType userType);
    boolean isUserIdExists(String userId);
    String encodePassword(String password);
    UserDTO createUser(UserDTO userDTO);

    @Transactional
    void updateProfile(UserDTO userDTO);
}