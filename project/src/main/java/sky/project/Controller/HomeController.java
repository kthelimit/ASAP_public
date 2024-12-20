package sky.project.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.UserDTO;
import sky.project.Repository.UserRepository;
import sky.project.Service.UserService;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
            userService.updateProfile(userDTO);
            model.addAttribute("message", "프로필이 성공적으로 수정되었습니다.");
            // 세션을 통해 사용자 데이터 갱신
            session.setAttribute("user", userDTO);
        } catch (Exception e) {
            model.addAttribute("error", "프로필 수정 중 오류가 발생했습니다.");
        }


        return "redirect:/profile/" + userDTO.getUserId();
    }


}
