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
        Product entity = dtoToEntity(dto);
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
