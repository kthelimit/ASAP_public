package sky.project.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.Service.MaterialService;
import sky.project.Service.SupplierService;

@Controller
@RequestMapping("/material")
public class MaterialController {

    @Autowired
  private MaterialService materialService;
    @Autowired
  private SupplierService supplierService;

    @GetMapping("/list")
    public String getMaterialsList(Model model, @RequestParam(defaultValue = "1") int page, HttpSession session) {
        // 자재 목록 페이징 처리
        Page<MaterialDTO> materials = materialService.getMaterials(PageRequest.of(page - 1, 10));
        model.addAttribute("materials", materials.getContent());
        model.addAttribute("totalPages", materials.getTotalPages());
        model.addAttribute("currentPage", page);

        // 세션에서 userId 가져와서 supplierName 조회 후 세션과 모델에 저장
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            String supplierName = supplierService.getSupplierNameByUserId(userId);
            session.setAttribute("supplierName", supplierName); // 세션에 저장
            model.addAttribute("supplierName", supplierName); // 모델에 추가
        }

        // 등록 폼에 사용될 빈 materialDTO 설정
        MaterialDTO materialDTO = new MaterialDTO();
        model.addAttribute("materialDTO", materialDTO);

        return "Material/MaterialList"; // 자재 목록과 등록 폼이 있는 동일 페이지
    }

    @PostMapping("/register")
    public String registerMaterial(@ModelAttribute MaterialDTO materialDTO,
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   HttpSession session) {
        String supplierId = (String) session.getAttribute("supplierId");
        materialDTO.setSupplierId(supplierId);
        materialService.registerMaterial(materialDTO, imageFile);
        return "redirect:/material/list";
    }
}