package sky.project.Service;

import sky.project.DTO.UserDTO;
import sky.project.Entity.UserType;

public interface UserService {
    UserDTO authenticate(String userId, String password, UserType userType);
    boolean isUserIdExists(String userId);
    String encodePassword(String password);
    UserDTO createUser(UserDTO userDTO);

}