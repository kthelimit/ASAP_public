package sky.project.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.UserDTO;
import sky.project.Service.UserService;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

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


