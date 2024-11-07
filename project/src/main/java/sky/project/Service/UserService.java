package sky.project.Service;

import sky.project.DTO.UserDTO;

public interface UserService {
    UserDTO authenticate(String userId, String password, String userType);
    boolean isUserIdExists(String userId);
    String encodePassword(String password);
    UserDTO createUser(UserDTO userDTO);
}
