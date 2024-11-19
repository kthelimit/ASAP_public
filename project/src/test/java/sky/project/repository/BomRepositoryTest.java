package sky.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.project.Entity.Bom;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Repository.BomRepository;

@SpringBootTest
public class BomRepositoryTest {
    @Autowired
    private BomRepository repository;

    @Test
    public void insertBom(){
        Product product = Product.builder().productCode("DB에 있는 코드를 여기다 넣어야 됨").build();
        Material material = Material.builder().materialCode("자재코드 예시").build();

        Bom bom = Bom.builder()
                .product(product)
                .material(material)
                .componentType("테스트")
                .requireQuantity(30)
                .build();
        repository.save(bom);
    }
}
