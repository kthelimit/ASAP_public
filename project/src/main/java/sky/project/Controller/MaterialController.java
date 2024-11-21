package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.MaterialDTO;
import sky.project.DTO.StockDTO;
import sky.project.Entity.Supplier;
import sky.project.Service.MaterialService;
import sky.project.Service.StockService;
import sky.project.Service.SupplierService;

import java.util.List;


@Controller
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private StockService stockService;

    @GetMapping("/list")
    public String getMaterialsList(Model model, @RequestParam(defaultValue = "1") int page) {
        // 자재 목록 페이징 처리 (예시)
        Page<MaterialDTO> materials = materialService.getMaterials(PageRequest.of(page - 1, 10));
        model.addAttribute("materials", materials.getContent());
        model.addAttribute("totalPages", materials.getTotalPages());
        model.addAttribute("currentPage", page);

        // 승인된 공급사 목록 조회 후 모델에 추가
        List<Supplier> approvedSuppliers = supplierService.getApprovedSuppliers();
        model.addAttribute("approvedSuppliers", approvedSuppliers);

        // 등록 폼에 사용될 빈 materialDTO 설정
        model.addAttribute("materialDTO", new MaterialDTO());

        return "Material/MaterialList"; // 자재 목록과 등록 폼이 있는 동일 페이지
    }

    @PostMapping("/register")
    public String registerMaterial(@ModelAttribute MaterialDTO materialDTO,
                                   @RequestParam("imageFile") MultipartFile imageFile) {
        materialService.registerMaterial(materialDTO, imageFile);
        return "redirect:/material/list";
    }

    //자재 입고
    @RequestMapping("/import")
    public String importMaterial(Model model) {
        return "/import/index";
    }

    //자재 출고
    @RequestMapping("/export")
    public String exportMaterial(Model model) {
        return "/export/index";
    }

    //창고 자재 목록
    @RequestMapping("/stocklist")
    public String stocklist(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<StockDTO> stocks = stockService.getStocks(PageRequest.of(page - 1, 10));
        model.addAttribute("stocks", stocks.getContent());

        return "/Stock/stocklist";
    }

    //창고 자재 등록
    @PostMapping("/registerStock")
    public String registerStock(@ModelAttribute StockDTO stockDTO) {
        stockService.register(stockDTO);
        return "redirect:/material/stocklist";
    }
}
