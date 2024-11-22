package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String stocklist(Model model, @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<StockDTO> stocks;

        if (keyword != null && !keyword.isEmpty()) {
            if (type.contains("t")) {
                stocks = stockService.getStocksWithSearchInMaterialType(keyword, pageable);
            } else if (type.contains("p")) {
                stocks = stockService.getStocksWithSearchInComponentType(keyword, pageable);
            } else if (type.contains("n")) {
                stocks = stockService.getStocksWithSearchInMaterialName(keyword, pageable);
            } else if (type.contains("c")) {
                stocks = stockService.getStocksWithSearchInMaterialCode(keyword, pageable);
            } else {
                stocks = stockService.getStocks(pageable);
            }
        } else {
            stocks = stockService.getStocks(pageable);
        }
        model.addAttribute("stocks", stocks.getContent());
        model.addAttribute("totalPages", stocks.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "/Stock/stocklist";
    }

    //창고 자재 등록
    @PostMapping("/registerStock")
    public String registerStock(@ModelAttribute StockDTO stockDTO) {
        stockService.register(stockDTO);
        return "redirect:/material/stocklist";
    }
}
