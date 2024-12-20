package sky.project.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.Entity.UserType;
import sky.project.DTO.UserDTO;
import sky.project.Entity.UserType;

import java.util.Optional;

public interface UserService {
    UserDTO authenticate(String userId, String password, UserType userType);
    boolean isUserIdExists(String userId);
    String encodePassword(String password);
    UserDTO createUser(UserDTO userDTO);
    Page<UserDTO> findAllUsers(Pageable pageable);
    Page<UserDTO> searchUsers(String keyword, Pageable pageable);
    void updateUserType(String userId, String newUserType);

    @Transactional
    void updateProfile(UserDTO userDTO);
}