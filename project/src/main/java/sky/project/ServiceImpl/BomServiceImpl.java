package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.BomDTO;
import sky.project.Entity.Bom;
import sky.project.Entity.Product;
import sky.project.Repository.BomRepository;
import sky.project.Repository.ProductRepository;
import sky.project.Service.BomService;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BomRepository repository;
    private final ProductRepository productRepository;

    //등록
    @Override
    public Long register(BomDTO dto) {
        Product product = productRepository.findByProductCode(dto.getProductCode());
        Bom bom = Bom.builder()
                .product(product)
                .materialName(dto.getMaterialName())
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

    //상품코드로 불러오기
    public List<BomDTO> findWithProductCode(String productCode) {
        List<Bom> bomList = repository.findByProductCode(productCode);
        List<BomDTO> bomDTOList = new ArrayList<>();
        bomList.forEach(bom -> {
            bomDTOList.add(entityToDTO(bom));
        });
        return bomDTOList;
    }

}
