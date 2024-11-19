package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.BomDTO;
import sky.project.Entity.Bom;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Repository.BomRepository;
import sky.project.Service.BomService;

@Service
@Log4j2
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BomRepository repository;

    //등록
    @Override
    public Long register(BomDTO dto) {
        Product product = Product.builder().productCode(dto.getProductCode()).build();
        Material material = Material.builder().materialCode(dto.getMaterialCode()).build();

        Bom bom = Bom.builder()
                .product(product)
                .material(material)
                .componentType(dto.getComponentType())
                .requireQuantity(dto.getRequireQuantity())
                .build();
        repository.save(bom);
        return bom.getBomId();

    }

    //수정
    @Override
    public void modify(BomDTO dto) {

    }

    //삭제
    @Override
    public void remove(Long BomId) {

    }
}
