package sky.project.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.project.DTO.SupplierDTO;
import sky.project.DTO.UserDTO;
import sky.project.Entity.Supplier;
import sky.project.Service.SupplierService;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/list")
    public String getSuppliersList(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // page를 1 감소시켜서 사용
        Page<Supplier> supplierPage = supplierService.getAllSuppliers(pageable);

        model.addAttribute("suppliers", supplierPage.getContent());
        model.addAttribute("totalPages", supplierPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "Supplier/SupplierList"; // Thymeleaf 뷰 이름
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        if (supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("alertMessage", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "/sample/admin";
        }

        model.addAttribute("supplierDTO", new SupplierDTO());
        return "Supplier/SupplierRegister";
    }
    @PostMapping("/register")
    public String registerSupplier(@ModelAttribute SupplierDTO supplierDTO, HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("user"); // UserDTO로 가져오기

        // 세션에 user가 없는 경우 처리
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        String userId = user.getUserId(); // userId를 UserDTO에서 가져옴

        // 이미 등록된 공급업체인지 확인
        if (supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("message", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "Supplier/SupplierRegister";
        }

        // userId 설정 후 등록 로직 실행
        supplierDTO.setUserId(userId);
        supplierService.registerSupplier(supplierDTO);

        return "redirect:/sample/admin";
    }

    @GetMapping("/pending")
    public String showPendingApprovals(Model model,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        List<SupplierDTO> suppliers = supplierService.getPendingApprovals(page, size);
        int totalPages = (int) Math.ceil((double) supplierService.getTotalPendingCount() / size);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "Supplier/PenddingApprovals";
    }

    @PostMapping("/approve/{supplierId}")
    public String approveSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        System.out.println("===============================================" +supplierId);
        try {
            supplierService.approveSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending"; // 오류 메시지와 함께 리다이렉트
        }
        return "redirect:/suppliers/pending";
    }

    @PostMapping("/reject/{supplierId}")
    public String rejectSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        try {
            supplierService.rejectSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending"; // 오류 메시지와 함께 리다이렉트
        }
        return "redirect:/suppliers/pending";
    }


    @GetMapping("/detail/{id}")
    public String getSupplierDetail(@PathVariable String id, Model model) {
        SupplierDTO supplierDTO = supplierService.getSupplierById(id);
        model.addAttribute("supplierDTO", supplierDTO);  // 모델에 문의 정보 추가
        return "Supplier/SupplierDetail";  // 문의 상세 페이지 (qnaDetail.html)로 이동
    }

}
