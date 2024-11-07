package sky.project.ServiceImpl;

import sky.project.DTO.UserDTO;
import sky.project.Entity.User;
import sky.project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.project.Service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        User user = convertToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    @Override
    public Page<UserDTO> getAllUsersPageable(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }
    @Override
    public Page<UserDTO> searchUserList(String query, Pageable pageable) {
        Page<User> userPage = userRepository.findByUserIdContainingIgnoreCase(query,pageable);
        return userPage.map(this::convertToDTO);
    }

    @Override
    public UserDTO getUserById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return convertToDTO(userOptional.get());
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDTO.getUsername());
            user.setUserAddress(userDTO.getUserAddress());

            // 비밀번호를 업데이트할 경우 암호화
            if (!userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            user.setEmail(userDTO.getEmail());
            user.setPhone(userDTO.getPhone());
            user.setBirthdate(userDTO.getBirthdate());

            User updatedUser = userRepository.save(user);
            return convertToDTO(updatedUser);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @Transactional
    @Override
    public void updateUserType(String id, UserDTO userDTO) {
        if (userDTO.getUserType().equals("USER")) {
            userDTO.setUserType("SHELTER");
        } else if (userDTO.getUserType().equals("SHELTER")) {
            userDTO.setUserType("USER");
        }

        userRepository.updateUserByUserId(id, userDTO.getUserType());
    }


    @Override
    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
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
            if ("USER".equals(userType) || "ADMIN".equals(userType) ) {
                // 일반 사용자일 경우
                if ("USER".equals(user.getUserType()) || "ADMIN".equals(user.getUserType())) {
                    return convertToDTO(user);
                } else {
                    throw new IllegalArgumentException("유저 타입이 일치하지 않습니다. 보호소 운영자 계정으로 로그인하세요.");
                }
            } else if ("SHELTER".equals(userType) || "ADMIN".equals(userType)) {
                // 보호소 운영자일 경우
                if ("SHELTER".equals(user.getUserType()) || "ADMIN".equals(user.getUserType())) {
                    return convertToDTO(user); // Shelter 관련 DTO로 변환
                } else {
                    throw new IllegalArgumentException("유저 타입이 일치하지 않습니다.  일반 사용자 계정으로 로그인하세요.");
                }
            }else {
                // 유효하지 않은 유저 타입
                throw new IllegalArgumentException("유효하지 않은 유저 타입입니다.");
            }
        } else {
            // 아이디와 비밀번호가 맞지 않으면 인증 실패
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }



    private User convertToEntity(UserDTO userDTO) {
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

    private UserDTO convertToDTO(User user) {
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
