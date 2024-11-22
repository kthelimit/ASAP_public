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
import java.util.Optional;


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
    public String getMaterialsList(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(value = "keyword", required = false) String keyword) {
        if (page < 1) {
            page = 1; // 페이지 값이 1 미만이면 기본값으로 설정
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MaterialDTO> materials;

        String searchKeyword = Optional.ofNullable(keyword).orElse("");

        try {
            if (!searchKeyword.isEmpty()) {
                materials = materialService.searchMaterials(searchKeyword, pageable);
            } else {
                materials = materialService.getMaterials(pageable);
            }

            if (materials.isEmpty()) {
                model.addAttribute("materials", List.of()); // 빈 목록 추가
                model.addAttribute("message", "검색 결과가 없습니다."); // 검색 결과 없을 시 메시지
            } else {
                model.addAttribute("materials", materials.getContent());
            }

            model.addAttribute("totalPages", materials.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", searchKeyword); // 검색어 유지
        } catch (Exception e) {
            model.addAttribute("error", "데이터를 가져오는 중 오류가 발생했습니다.");
            return "errorPage"; // 에러 페이지로 리다이렉트
        }

        // 승인된 공급사 목록 조회
        model.addAttribute("approvedSuppliers", supplierService.getApprovedSuppliers());

        // 등록 폼에 빈 MaterialDTO 추가
        model.addAttribute("materialDTO", new MaterialDTO());

        return "Material/MaterialList"; // 자재 목록 및 등록 폼이 있는 HTML 페이지
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
