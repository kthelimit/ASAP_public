package sky.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.*;
import sky.project.Entity.Product;
import sky.project.Service.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class APIController {
    private final ProductService productService;
    private final BomService bomService;
    private final MaterialService materialService;
    private final AssyService assyService;
    private final ExportService exportService;

    //* 상품 *//
    //등록
    @PostMapping("/post/product")
    public ResponseEntity<Long> registerProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.register(dto));
    }

    //등록한 상품을 select에 불러오는 용도
    @RequestMapping("/load/product")
    public List<Product> loadProductInfo() {
        return productService.getProductList();
    }

    //상품 선택에 따른 BOM리스트 출력용
    @RequestMapping("/load/bom/{productCode}")
    public List<BomDTO> loadBomWithProductInfo(@PathVariable String productCode) {
        return bomService.findWithProductCode(productCode);
    }

    //* BOM *//
    //등록
    @PostMapping("/post/bom")
    public ResponseEntity<Long> registerBom(@RequestBody BomDTO dto) {
        return ResponseEntity.ok(bomService.register(dto));
    }

    //BOM 삭제
    @DeleteMapping("/delete/bom/{bomId}")
    public ResponseEntity<String> deleteBom(@PathVariable String bomId) {
        log.info("Delete bom with id {}", bomId);
        bomService.remove(Long.parseLong(bomId));
        return ResponseEntity.ok(bomId);
    }

    //부품 종류 선택에 따른 자재 리스트 출력용
    @RequestMapping("/load/material/{componentType}")
    public List<MaterialDTO> loadMaterialByComponentType(@PathVariable String componentType) {
        return materialService.findByComponentType(componentType);
    }

    //자재 타입 선택에 따른 자재 리스트 출력용
    @RequestMapping("/load/materialWith/{materialType}")
    public List<MaterialDTO> loadMaterialByMaterialType(@PathVariable String materialType) {
        return materialService.findByMaterialType(materialType);
    }

    //ProductCode에 따른 조립 공정 출력용
    @RequestMapping("/load/Assy/{productCode}")
    public List<MaterialDTO> loadAssyByProductCode(@PathVariable String productCode) {
        return materialService.findAssyMaterialByProductCode(productCode);
    }


    //ProductCode에 따른 조립 공정 출력용
    @RequestMapping("/load/AssyList/{assyMaterialCode}")
    public List<AssyDTO> loadAssyMaterialListByProductCode(@PathVariable String assyMaterialCode) {
        log.info("assyMaterialCode: "+assyMaterialCode);
        return assyService.findByAssyMaterialCode(assyMaterialCode);
    }


    //출고요청 등록
    @PostMapping("/post/export")
    public ResponseEntity<Long> registerExport(@RequestBody ExportDTO dto){
        return ResponseEntity.ok(exportService.register(dto));
    }


}
