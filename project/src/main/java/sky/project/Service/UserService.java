package sky.project.Service;

import sky.project.DTO.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(String id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(String id, UserDTO userDTO);
    void deleteUser(String id);
    UserDTO authenticate(String userId, String password, String userType); // 인증 메서드 추가
    public boolean isUserIdExists(String userId);
    // 비밀번호 암호화 메소드 추가
    String encodePassword(String password);
    public void updateUserType(String id, UserDTO userDTO);
    Page<UserDTO> getAllUsersPageable(Pageable pageable);
    public Page<UserDTO> searchUserList(String query, Pageable pageable);
}