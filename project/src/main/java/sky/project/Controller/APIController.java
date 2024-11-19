package sky.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.project.DTO.ProductDTO;
import sky.project.Entity.Product;
import sky.project.Service.ProductService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class APIController {
    private final ProductService productService;

    //* 상품 *//
    //등록
    @PostMapping("/post/product")
    public ResponseEntity<Long> registerProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.register(dto));
    }

    @RequestMapping("/load/product")
    public List<Product> loadProductInfo(){
        return productService.getProductList();
    }
}
