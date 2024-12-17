package sky.project.ServiceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.BomDTO;
import sky.project.Entity.Bom;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Entity.Stock;
import sky.project.Repository.BomRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.ProductRepository;
import sky.project.Repository.StockRepository;
import sky.project.Service.BomService;
import sky.project.Service.OrderService;
import sky.project.Service.StockService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BomRepository repository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final BomRepository bomRepository;
    private final OrderService orderService;

    //등록
    @Override
    public Long register(BomDTO dto) {

        //해당 상품에 같은 자재로 bom이 등록되어 있는지 확인하고, 없으면 새로 등록한다.
        if (bomRepository.findByProductCodeAndMaterialCode(dto.getProductCode(), dto.getMaterialCode()) != null) {

            Bom bom = bomRepository.findByProductCodeAndMaterialCode(dto.getProductCode(), dto.getMaterialCode());
            bom.setComponentType(dto.getComponentType());
            bom.setRequireQuantity(dto.getRequireQuantity());
            repository.save(bom);
            return bom.getBomId();

        } else {

            Product product = productRepository.findByProductCode(dto.getProductCode());

            if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
                Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();

                Bom bom = Bom.builder()
                        .product(product)
                        .material(material)
                        .componentType(dto.getComponentType())
                        .requireQuantity(dto.getRequireQuantity())
                        .build();
                repository.save(bom);
                return bom.getBomId();
            } else {
                return null;
            }
        }
    }

    //삭제
    @Transactional
    @Override
    public void remove(Long BomId) {
        repository.deleteById(BomId);
    }

    //상품코드로 불러오기
    @Override
    public List<BomDTO> findWithProductCode(String productCode) {
        List<Bom> bomList = repository.findByProductCode(productCode);
        List<BomDTO> bomDTOList = new ArrayList<>();
        bomList.forEach(bom -> {
            bomDTOList.add(entityToDTO(bom));
        });
        return bomDTOList;
    }

    @Override
    public List<Bom> getbomList() {
        return repository.findAll();
    }

    public BomDTO entityToDTO(Bom entity) {

        //조달계획 출력용 가용재고 계산(현재 창고 재고 + 업체에 발주넣어둔 남은 수량의 합)
        Stock stock = stockRepository.findByMaterialCode(entity.getMaterial().getMaterialCode());
        int availableStock = stockService.calculateAvailableStock(stock);
        int remainedOrderQuantity = orderService.calculateRemainedQuantityForBOMDTO(stock.getMaterial());

        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(entity.getMaterial().getLeadTime());
        return BomDTO.builder()
                .bomId(entity.getBomId())
                .componentType(entity.getComponentType())
                .materialCode(entity.getMaterial().getMaterialCode())
                .materialName(entity.getMaterial().getMaterialName())
                .productCode(entity.getProduct().getProductCode())
                .requireQuantity(entity.getRequireQuantity())
                .dayAfterLeadTime(date)
                .availableStock(availableStock)
                .remainedOrderQuantity(remainedOrderQuantity)
                .build();
    }

}
