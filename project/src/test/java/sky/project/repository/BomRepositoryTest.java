package sky.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.project.Entity.Bom;
import sky.project.Entity.Product;
import sky.project.Repository.BomRepository;

@SpringBootTest
public class BomRepositoryTest {
    @Autowired
    private BomRepository repository;

    @Test
    public void insertBom(){
        Product product = Product.builder().productCode("MATB3FIN001").build();

        Bom bom = Bom.builder()
                .product(product)
                .materialName("알루미늄 더블림 29인치")
                .componentType("테스트")
                .requireQuantity(30)
                .build();
        repository.save(bom);
    }
}
