package sky.project.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.UserDTO;
import sky.project.Service.UserService;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO userDTO, HttpServletRequest request, Model model) {
        try {
            // 로그인 폼에서 전달된 userId와 password로 인증
            if (userDTO.getUserId() == null || userDTO.getUserId().isEmpty()) {
                model.addAttribute("error", "User ID is required");
                return "/UserForm/Login"; // userId가 없으면 오류 반환
            }

            UserDTO authenticatedUser = userService.authenticate(userDTO.getUserId(), userDTO.getPassword(), userDTO.getUserType());

            if (authenticatedUser != null) {
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(60 * 60 * 12); // 세션 유효시간을 12시간으로 설정
                session.setAttribute("user", authenticatedUser);
                return "redirect:/Main"; // 메인으로
            } else {
                model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
                return "Login";
            }
        } catch (IllegalArgumentException e) {
            // 예외 발생 시 오류 메시지 추가
            model.addAttribute("error", e.getMessage());
            return "/UserForm/Login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return "redirect:/";
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public boolean checkUserId(@RequestParam String userId) {
        return !userService.isUserIdExists(userId);
    }

    @GetMapping("")
    public String showLoginForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "/UserForm/Login";
    }
}
