package sky.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.project.DTO.BomDTO;
import sky.project.DTO.ProductDTO;
import sky.project.Entity.Product;
import sky.project.Service.BomService;
import sky.project.Service.ProductService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class APIController {
    private final ProductService productService;
    private final BomService bomService;

    //* 상품 *//
    //등록
    @PostMapping("/post/product")
    public ResponseEntity<Long> registerProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.register(dto));
    }

    //등록한 상품을 select에 불러오는 용도
    @RequestMapping("/load/product")
    public List<Product> loadProductInfo(){
        return productService.getProductList();
    }



    @RequestMapping("/load/bom/{productCode}")
    public List<BomDTO> loadBomWithProductInfo(@PathVariable String productCode){
        return bomService.findWithProductCode(productCode);
    }
//
//    //부품 종류로 자재 리스트 불러오기
//    @RequestMapping("/load/material/{componentType}")
//    public HashMap<String, String> loadMaterialInfo(@PathVariable String componentType){
//        log.info(componentType);
//        List<Material> materialList= materialService.getMaterialListWithComponentType(componentType);
//        HashMap<String, String> materialInfo = new HashMap<>();
//        materialList.forEach(material -> {
//            materialInfo.put(material.getMaterialName(), material.getMaterialCode());
//        });
//        return materialInfo;
//    }
}
