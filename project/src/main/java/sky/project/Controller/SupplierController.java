package sky.project.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.project.DTO.SupplierDTO;
import sky.project.DTO.SupplierStockDTO;
import sky.project.DTO.UserDTO;
import sky.project.Service.SupplierService;
import sky.project.Service.SupplierStockService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierStockService supplierStockService;

    @GetMapping("/list")
    public String getSuppliersList(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "12") int size,
                                   @RequestParam(value = "keyword", required = false) String keyword) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SupplierDTO> supplier;
        if (keyword != null && !keyword.isEmpty()) {
            supplier = supplierService.searchSuppliers(keyword, pageable);
        } else {
            supplier = supplierService.getAllSuppliers(pageable);
        }

        model.addAttribute("suppliers", supplier.getContent());
        model.addAttribute("totalPages", supplier.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "Supplier/SupplierList";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }
        String userId = user.getUserId();

        if (userId != null && supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("alertMessage", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "/sample/admin";
        }

        model.addAttribute("supplierDTO", new SupplierDTO());
        return "Supplier/SupplierRegister";
    }

    @PostMapping("/register")
    public String registerSupplier(@ModelAttribute SupplierDTO supplierDTO,
                                   @RequestParam("contractFile") MultipartFile file,
                                   HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }
        String userId = user.getUserId();
        supplierDTO.setSupplierId(userId);  // supplierId 설정

        if (supplierService.isAlreadyRegistered(userId)) {
            model.addAttribute("message", "이미 공급업체 등록을 마친 상태입니다. 승인을 기다려주세요.");
            return "Supplier/SupplierRegister";
        }

        try {
            if (!file.isEmpty()) {
                String directoryPath = "C:/uploads/File";
                Path directory = Paths.get(directoryPath);
                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }
                String filePath = directoryPath + "/" + file.getOriginalFilename();
                file.transferTo(new File(filePath));
                supplierDTO.setContractFilePath("/uploads/File" + file.getOriginalFilename());
            }
            supplierDTO.setUserId(userId);
            supplierService.registerSupplier(supplierDTO);
            return "redirect:/sample/admin";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 업로드에 실패했습니다.");
            return "Supplier/SupplierRegister";
        }
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
        return "Supplier/PendingApprovals";
    }

    @PostMapping("/approve/{supplierId}")
    public String approveSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        try {
            supplierService.approveSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending";
        }
        return "redirect:/suppliers/pending";
    }

    @PostMapping("/reject/{supplierId}")
    public String rejectSupplier(@PathVariable String supplierId, RedirectAttributes redirectAttributes) {
        try {
            supplierService.rejectSupplier(supplierId);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 ID를 가진 공급업체를 찾을 수 없습니다.");
            return "redirect:/suppliers/pending";
        }
        return "redirect:/suppliers/pending";
    }

    @GetMapping("/detail/{id}")
    public String getSupplierDetail(@PathVariable String id, Model model) {
        SupplierDTO supplierDTO = supplierService.getSupplierById(id);
        model.addAttribute("supplierDTO", supplierDTO);
        return "Supplier/SupplierDetail";
    }


    @PostMapping("/stockUpdate")
    public String supplierStockUpdate(SupplierStockDTO dto){
        supplierStockService.updateStock(dto);
        return "redirect:/suppliers/page";
    }

    @GetMapping("/page")
    public String supplierPage(@SessionAttribute(name= "user", required = false) UserDTO user, Model model) {
        if(user==null){
            model.addAttribute("message", "로그인이 필요합니다.");
            return "redirect:/";
        }
        String userId = user.getUserId();

        System.out.println("userId : " + userId);
        model.addAttribute("supplierStocks", supplierStockService.findBySupplierId(userId));

        return "Supplier/SupplierPage";
    }

//    @GetMapping("/page")
//    public String supplierPage(Model model, HttpSession session) {
//        UserDTO user = (UserDTO) session.getAttribute("user");
//        if (user == null) {
//            model.addAttribute("message", "로그인이 필요합니다.");
//            return "redirect:/";
//        }
//        String userId = user.getUserId();
//
//        System.out.println("userId : " + userId);
//        model.addAttribute("supplierStocks", supplierStockService.findBySupplierId(userId));
//
//        return "Supplier/SupplierPage";
//    }


}
