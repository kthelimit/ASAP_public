package sky.project.Service;
import sky.project.Entity.UserType;
import sky.project.DTO.UserDTO;

public interface UserService {
    UserDTO authenticate(String userId, String password, UserType userType);
    boolean isUserIdExists(String userId);
    String encodePassword(String password);
    UserDTO createUser(UserDTO userDTO);

}