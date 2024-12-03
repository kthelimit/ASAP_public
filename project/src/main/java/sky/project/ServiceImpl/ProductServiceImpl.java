package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProductDTO;
import sky.project.Entity.Product;
import sky.project.Repository.ProductRepository;
import sky.project.Service.ProductService;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public Long register(ProductDTO dto) {
        Product entity;
        //만약 이미 해당 상품 코드가 등록되어 있는 경우 덮어 쓰기한다(상품코드가 중복되어서는 안되므로)
        if (repository.findByProductCode(dto.getProductCode()) != null) {
            entity = repository.findByProductCode(dto.getProductCode());
            entity.setProductName(dto.getProductName());
        } else {
            entity = dtoToEntity(dto);
        }
        repository.save(entity);
        return entity.getProductId();
    }

    @Override
    public List<Product> getProductList() {
        return repository.findAll();
    }

    public Product dtoToEntity(ProductDTO dto) {

        Product entity = Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productCode(dto.getProductCode())
                .build();
        return entity;
    }
}
