package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.project.DTO.UserDTO;
import sky.project.Entity.Supplier;
import sky.project.Entity.User;
import sky.project.Entity.UserType;
import sky.project.Repository.SupplierRepository;
import sky.project.Repository.UserRepository;
import sky.project.Service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public boolean isUserIdExists(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password); // PasswordEncoder를 이용해 비밀번호를 암호화합니다.
    }

    @Transactional
    @Override
    public void updateProfile(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 업데이트 필드 설정
        user.setUserId(userDTO.getUserId());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        user.setUserAddress(userDTO.getUserAddress());

        userRepository.save(user); // 저장
    }

    @Override
    public UserDTO findUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return new UserDTO(user);
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
        User user = toEntity(userDTO);

        //admin 계정인 경우 권한 넣어주기
        if (userDTO.getUserId().equals("admin")) {
            user.setUserType(UserType.ADMIN);
        }
        User savedUser = userRepository.save(user);

        //admin 계정인 경우 DG전동 업체 정보를 넣어주기
        if (userDTO.getUserId().equals("admin")) {
            Supplier supplier = Supplier.builder()
                    .user(savedUser)
                    .supplierId(savedUser.getUserId())
                    .supplierName("DG전동")
                    .businessType("제조업")
                    .businessItem("자전거")
                    .businessRegistrationNumber("000-00-00000")
                    .contactInfo("000-0000-0000")
                    .address(savedUser.getUserAddress())
                    .approved(true)
                    .build();
            supplierRepository.save(supplier);

            //부서 계정 넣어주기
            makeDeptUser();
        }
        return toDTO(savedUser);
    }

    @Override
    public Page<UserDTO> searchUsers(String keyword, Pageable pageable){

        Page<User> users = userRepository.findByUserIdContaining(keyword, pageable);

        return users.map(this::toDTO);
    }

    @Override
    public UserDTO authenticate(String userId, String password, UserType userType) {
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
            if (UserType.ADMIN.equals(userType) || UserType.PURCHASE_DEPT.equals(userType) || UserType.PRODUCTION_DEPT.equals(userType) || UserType.MATERIAL_DEPT.equals(userType)) {
                if (UserType.ADMIN.equals(user.getUserType()) || UserType.PURCHASE_DEPT.equals(user.getUserType()) || UserType.PRODUCTION_DEPT.equals(user.getUserType()) || UserType.MATERIAL_DEPT.equals(user.getUserType())) {
                    return toDTO(user);
                } else {
                    throw new IllegalArgumentException("유저 타입이 일치하지 않습니다. 협력사 계정으로 로그인하세요.");
                }
            } else if (UserType.PARTNER.equals(userType) || UserType.SUPPLIER.equals(userType)) {
                if (UserType.PARTNER.equals(user.getUserType()) || UserType.SUPPLIER.equals(user.getUserType())) {
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

    @Override
    public void updateUserType(String userId, String newUserType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        try {
            UserType userType = UserType.valueOf(newUserType.toUpperCase()); // String -> Enum 변환
            user.setUserType(userType); // Enum 값을 설정
            userRepository.save(user); // 변경 사항 저장
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 유저타입입니다: " + newUserType);
        }
    }


    @Override
    public Page<UserDTO> findAllUsers(Pageable pageable) {

        Page<User> users = userRepository.findByUserTypeNot(UserType.SUPPLIER, pageable);
       return users.map(this::toDTO);
    }

    private User toEntity(UserDTO userDTO) { //DTO를 Entity로 변경
        return User.builder()
                .userId(userDTO.getUserId())
                .username(userDTO.getUsername())
                .userAddress(userDTO.getUserAddress())
                .password(userDTO.getPassword())
                .userType(userDTO.getUserType())
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
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthdate(user.getBirthdate())
                .createdDate((user.getCreatedDate()))
                .modifiedDate((user.getModifiedDate()))
                .build();
    }

    private void makeDeptUser(){
        User userDEPT1 = User.builder()
                .userId("dept1")
                .username("구매부서 직원")
                .userAddress("주소")
                .email("email@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .phone("000-0000-0000")
                .userType(UserType.PURCHASE_DEPT)
                .build();
        userRepository.save(userDEPT1);

        User userDEPT2 = User.builder()
                .userId("dept2")
                .username("자재부서 직원")
                .userAddress("주소")
                .email("email@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .phone("000-0000-0000")
                .userType(UserType.MATERIAL_DEPT)
                .build();
        userRepository.save(userDEPT2);

        User userDEPT3 = User.builder()
                .userId("dept3")
                .username("생산부서 직원")
                .userAddress("주소")
                .email("email@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .phone("000-0000-0000")
                .userType(UserType.PRODUCTION_DEPT)
                .build();
        userRepository.save(userDEPT3);
    }



}