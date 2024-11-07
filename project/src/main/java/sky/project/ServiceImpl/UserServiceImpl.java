package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sky.project.DTO.UserDTO;
import sky.project.Entity.User; // User 엔티티 임포트
import sky.project.Repository.UserRepository;
import sky.project.Service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Override
    public boolean isUserIdExists(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password); // PasswordEncoder를 이용해 비밀번호를 암호화합니다.
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        // 아이디 유효성 검사
        if (userRepository.existsById(userDTO.getUserId())) { // 아이디가 이미 존재하는지 확인
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다."); // 적절한 예외를 던짐
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        userDTO.setCreationDate(LocalDate.now());
        User user = toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    public UserDTO authenticate(String userId, String password, String userType) {
        System.out.println("아이디 :" + userId);

        // 유저 아이디로 사용자 검색
        Optional<User> userOptional = userRepository.findByUserId(userId);

        // 유저가 존재하는지 확인
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
            }

            // 유저 타입에 따른 분기 처리
            if ("ADMIN".equals(userType)) {
                if ("ADMIN".equals(user.getUserType())) {
                    return toDTO(user);
                } else {
                    throw new IllegalArgumentException("유저 타입이 일치하지 않습니다. 협력사 계정으로 로그인하세요.");
                }
            } else if ("PARTNER".equals(userType) || "SUPPLIER".equals(userType)) {
                if ("PARTNER".equals(user.getUserType()) || "SUPPLIER".equals(user.getUserType())) {
                    return toDTO(user); // 협력사 관련 DTO로 전환
                } else {
                    throw new IllegalArgumentException("유저 타입이 일치하지 않습니다. 관리자 계정으로 로그인하세요.");
                }
            } else {
                throw new IllegalArgumentException("유효하지 않은 유저 타입입니다.");
            }
        } else {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    private User toEntity(UserDTO userDTO) { //DTO를 Entity로 변경
        return User.builder()
                .userId(userDTO.getUserId())
                .username(userDTO.getUsername())
                .userAddress(userDTO.getUserAddress())
                .password(userDTO.getPassword())
                .userType(userDTO.getUserType())
                .creationDate(userDTO.getCreationDate())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .birthdate(userDTO.getBirthdate())
                .build();
    }

    private UserDTO toDTO(User user) { // Entity 를 DTO로 변경
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .userAddress(user.getUserAddress())
                .password(user.getPassword())
                .userType(user.getUserType())
                .creationDate(user.getCreationDate())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthdate(user.getBirthdate())
                .build();
    }
}
