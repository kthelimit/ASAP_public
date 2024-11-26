package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.ExportDTO;
import sky.project.DTO.MaterialDTO;
import sky.project.DTO.StockDTO;
import sky.project.Service.*;

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

    @Autowired
    private ProductionPlanService productionPlanService;
    @Autowired
    private ExportService exportService;

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
    public String exportMaterial(Model model, @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(value = "type", required = false) String type,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page2,
                                 @RequestParam(defaultValue = "10") int size2,
                                 @RequestParam(value = "type2", required = false) String type2,
                                 @RequestParam(value = "keyword2", required = false) String keyword2) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable pageable2 = PageRequest.of(page2 - 1, size2);

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

        //현재 상태가 대기중인 출고 요청만 가지고 온다.
        Page<ExportDTO> exports;

        if(keyword2 != null && !keyword2.isEmpty()) {
            if (type2.contains("e")) {
                exports = exportService.getCurrentExportsWithSearchInExportCode(keyword2, pageable2);
            } else if (type2.contains("p")) {
                exports = exportService.getCurrentExportsWithSearchInProductionPlanCode(keyword2, pageable2);
            } else if (type2.contains("n")) {
                exports = exportService.getCurrentExportsWithSearchInMaterialName(keyword2, pageable2);
            } else if (type2.contains("c")) {
                exports = exportService.getCurrentExportsWithSearchInMaterialCode(keyword2, pageable2);
            } else if (type2.contains("d")) {
                exports = exportService.getCurrentExportsWithSearchInProductName(keyword2, pageable2);
            } else {
                exports = exportService.getCurrentExportListPage(pageable2);
            }
        }else{
            exports = exportService.getCurrentExportListPage(pageable2);
        }
        model.addAttribute("exports", exports.getContent());
        model.addAttribute("totalPages2", exports.getTotalPages());
        model.addAttribute("currentPage2", page2);
        model.addAttribute("pageSize2", size2);
        model.addAttribute("keyword2", keyword2);
        model.addAttribute("type2", type2);



        return "/Export/index";
    }

    //자재 출고요청
    @RequestMapping("/export/request")
    public String exportMaterialRequest(Model model) {
        model.addAttribute("productionPlans", productionPlanService.getProductionPlansWithDate());

        return "/Export/ExportRequest";
    }


    //출고 승인
    @PostMapping("/export/approval")
    public String exportMaterialApproval(ExportDTO dto) {
        exportService.modify(dto);
        return "redirect:/material/export";
    }

    //출고 내역
    @RequestMapping("/export/history")
    public String exportMaterialHistory(Model model, @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        
        //최근에 요청한 출고내역이 위쪽에 뜨도록
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("exportId").descending());

        Page<ExportDTO> exportDTOS;

        if(keyword != null && !keyword.isEmpty()) {
            if (type.contains("e")) {
                exportDTOS = exportService.getExportsWithSearchInExportCode(keyword, pageable);
            } else if (type.contains("p")) {
                exportDTOS = exportService.getExportsWithSearchInProductionPlanCode(keyword, pageable);
            } else if (type.contains("n")) {
                exportDTOS = exportService.getExportsWithSearchInMaterialName(keyword, pageable);
            } else if (type.contains("c")) {
                exportDTOS = exportService.getExportsWithSearchInMaterialCode(keyword, pageable);
            } else if (type.contains("d")) {
                exportDTOS = exportService.getExportsWithSearchInProductName(keyword, pageable);
            } else {
                exportDTOS = exportService.getExports(pageable);
            }
        }else{
            exportDTOS = exportService.getExports(pageable);
        }
        model.addAttribute("exports", exportDTOS.getContent());
        model.addAttribute("totalPages", exportDTOS.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        return "/Export/ExportHistory";
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
