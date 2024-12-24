package sky.project.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.UserDTO;
import sky.project.Entity.User;
import sky.project.Repository.UserRepository;
import sky.project.Service.UserService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 로그인 폼 표시
    @GetMapping({"/",""})
    public String showLoginForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "UserForm/Login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO userDTO, HttpServletRequest request, Model model) {
        try {
            if (userDTO.getUserId() == null || userDTO.getUserId().isEmpty()) {
                model.addAttribute("error", "User ID is required");
                return "UserForm/Login";
            }

            UserDTO authenticatedUser = userService.authenticate(userDTO.getUserId(), userDTO.getPassword(), userDTO.getUserType());

            if (authenticatedUser != null) {
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(60 * 60 * 12); // 세션 유효시간을 12시간으로 설정
                session.setAttribute("user", authenticatedUser);
                return "redirect:/main";
            } else {
                model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
                return "UserForm/Login";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "UserForm/Login";
        }
    }

    @GetMapping("/main")
    public String mainPage( ) {
//        UserDTO user = (UserDTO) session.getAttribute("user");
//        if(user == null) {
//            return "redirect:/";
//        }
//
//        model.addAttribute("user", user);
        return "redirect:/dashboard/index";
    }


    // 회원가입 폼 표시
    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "UserForm/SignUp";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO userDTO) {
        userService.createUser(userDTO); // 사용자 생성 로직
        return "redirect:/"; // 회원가입 성공 시 로그인 페이지로 이동
    }

    // ID 중복 확인 처리
    @GetMapping("/checkUserId")
    @ResponseBody
    public boolean checkUserId(@RequestParam String userId) {
        return !userService.isUserIdExists(userId);
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    // 개인 프로필 보기
    @GetMapping("/profile/{id}")
    public String showUserProfile(@PathVariable String id, HttpSession session, Model model) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");

        if (userDTO != null) {
            model.addAttribute("userDTO", userDTO); // 수정된 데이터 추가
        } else {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
        }

        model.addAttribute("userDTO", userDTO);
        return "UserForm/UserProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("userDTO") UserDTO userDTO, Model model, HttpSession session) {
        try {
            // 기존 사용자 데이터를 데이터베이스에서 가져옴
            UserDTO existingUser = userService.findUserById(userDTO.getUserId());
            if (existingUser == null) {
                model.addAttribute("error", "사용자를 찾을 수 없습니다.");
                return "redirect:/profile/" + userDTO.getUserId();
            }

            // 기존 데이터에서 필드를 보존
            userDTO.setCreatedDate(existingUser.getCreatedDate());
            userDTO.setModifiedDate(LocalDateTime.now()); // 수정 날짜는 현재 시간으로 설정

            // 데이터 저장
            userService.updateProfile(userDTO);

            // 성공 메시지와 세션 갱신
            model.addAttribute("message", "프로필이 성공적으로 수정되었습니다.");
            session.setAttribute("user", userDTO);
        } catch (Exception e) {
            model.addAttribute("error", "프로필 수정 중 오류가 발생했습니다.");
        }

        return "redirect:/profile/" + userDTO.getUserId();
    }

    //비밀번호 변경 페이지
    @GetMapping("/password/{id}")
    public String showPasswordForm(@PathVariable String id, HttpSession session, Model model) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO != null) {
            model.addAttribute("userDTO", userDTO);
        } else {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
        }
        model.addAttribute("userDTO", userDTO);
        return "UserForm/UpdatePassword";
    }

    // 비밀번호 변경 요청
    @PostMapping("/password/{id}")
    public String changePassword(@PathVariable String id,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session, Model model) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            model.addAttribute("error", "사용자가 찾을 수 없습니다.");
            return "redirect:/";
        }

        // 1. 현재 비밀번호가 DB의 비밀번호와 일치하는지 확인
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/password/" + userDTO.getUserId(); // 비밀번호가 일치하지 않으면 폼으로 돌아감
        }

        // 2. 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");

            return "redirect:/password/" + userDTO.getUserId(); // 비밀번호가 일치하지 않으면 폼으로 돌아감
        }

        // 3. 새 비밀번호를 DB에 저장
        user.setPassword(passwordEncoder.encode(newPassword)); // 새 비밀번호 암호화
        userRepository.save(user); // 저장
        model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");

        return "redirect:/profile/" + userDTO.getUserId(); // 비밀번호 변경 후 프로필 페이지로 리다이렉트
    }

    @GetMapping("/usertype")
    public String usertype(
            Model model,
            @RequestParam(defaultValue = "1") int page, // 기본 페이지: 1
            @RequestParam(defaultValue = "30") int size, // 기본 페이지 크기: 12
            @RequestParam(value = "keyword", required = false) String keyword // 검색 키워드
    ) {
        // 페이지는 0부터 시작하므로 1을 빼줌
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").ascending());

        // 검색어가 있는 경우와 없는 경우 처리
        Page<UserDTO> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userService.searchUsers(keyword, pageable); // 검색 메소드 호출
        } else {
            users = userService.findAllUsers(pageable); // 전체 사용자 조회
        }

        // 모델에 데이터 추가
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "UserForm/UserTypeModify";
    }


    @PostMapping("/{userId}/updateUserType")
    public ResponseEntity<String> updateUserType(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {

        String newUserType = request.get("userType");

        if (newUserType == null || newUserType.isEmpty()) {
            return ResponseEntity.badRequest().body("유저타입이 유효하지 않습니다.");
        }

        userService.updateUserType(userId, newUserType);
        return ResponseEntity.ok("유저타입이 변경되었습니다.");
    }
}


