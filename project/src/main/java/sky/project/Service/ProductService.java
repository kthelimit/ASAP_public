package sky.project.Service;

import sky.project.DTO.ProductDTO;
import sky.project.Entity.Product;

import java.util.List;

public interface ProductService {

    //등록
    Long register(ProductDTO dto);
    
    //상품 리스트 반환
    List<Product>getProductList();

    default Product dtoToEntity(ProductDTO dto) {

        Product entity = Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .build();
        return entity;
    }
}
