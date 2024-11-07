package sky.project.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sky.project.DTO.UserDTO;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserToModel(HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        model.addAttribute("user", user);
    }
}