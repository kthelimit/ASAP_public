package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.AssyDTO;
import sky.project.Entity.Assy;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Repository.*;
import sky.project.Service.AssyService;
import sky.project.Service.StockService;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AssyServiceImpl implements AssyService {

    private final MaterialRepository materialRepository;
    private final AssyRepository assyRepository;
    private final ProductRepository productRepository;
    private final ExportRepository exportRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final StockService stockService;
    private final StockRepository stockRepository;

    @Override
    public Long register(AssyDTO dto) {
        Assy entity = dtoToEntity(dto);
        assyRepository.save(entity);
        return entity.getAssyId();
    }

    @Override
    public List<AssyDTO> findByAssyMaterialCode(String assyMaterialCode) {
        List<Assy> assyList = assyRepository.findByAssyMaterialCode(assyMaterialCode);
        List<AssyDTO> assyDTOList = new ArrayList<>();
        assyList.forEach(assy -> {
            assyDTOList.add(entityToDto(assy));
        });
        return assyDTOList;
    }

    @Override
    public int findLeftQuantityByAssyMaterialCode(String productionPlanCode, String assyMaterialCode) {
        //해당 생산 계획에서 해당 조립품의 요구 수량(페달(1:2) 외에는 다 1:1대응임)
        int requiredQuantityForAssy = productionPlanRepository.findByProductionPlanCode(productionPlanCode).getProductionQuantity();

        //만약 조립된 페달A거나 조립된 페달B인 경우 2를 곱한다.
        if (assyMaterialCode.equals("MATB1SEM004") || assyMaterialCode.equals("MATB1SEM009")) {
            requiredQuantityForAssy *= 2;
        }
        int totalExportRequestQuantity;
        //해당 생산 계획에 대해서 출고 요청해둔 수량
        if (exportRepository.findCountByProductionPlanCodeAndAssyMaterialCode(productionPlanCode, assyMaterialCode) == 0) {
            totalExportRequestQuantity = 0;
        } else {

            //여기 수식을 바꿔야할 거 같음... 나누는 수량을 잘못한 것이다. 나누는 수량은 해당 조립품의 구성 품목 갯수여야함.
            int assyCount = assyRepository.findCountByAssyMaterialCode(assyMaterialCode);
            totalExportRequestQuantity = exportRepository.findSumByProductionPlanCodeAndAssyMaterialCode(productionPlanCode, assyMaterialCode)
                    / assyCount;
        }
        return requiredQuantityForAssy - totalExportRequestQuantity;
    }

    @Override
    public List<Assy> getAssys() {
        return assyRepository.findAll();
    }


    public Assy dtoToEntity(AssyDTO dto) {
        Material material;
        Material assyMaterial;
        Product product;
        if (materialRepository.findByMaterialCode(dto.getAssyMaterialCode()).isPresent() && materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
            material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            assyMaterial = materialRepository.findByMaterialCode(dto.getAssyMaterialCode()).get();
            product = productRepository.findByProductCode(dto.getProductCode());
            return Assy.builder()
                    .assyId(dto.getAssyId())
                    .product(product)
                    .assemblyMaterial(assyMaterial)
                    .material(material)
                    .quantity(dto.getQuantity())
                    .build();
        } else return null;
    }

    public AssyDTO entityToDto(Assy entity) {

        //가용재고 출력용
        int availableStock = stockService.calculateAvailableStock(stockRepository.findByMaterialCode(entity.getMaterial().getMaterialCode()));

        //재고 출력용
        int stock = stockRepository.findByMaterialCode(entity.getMaterial().getMaterialCode()).getQuantity();
        return AssyDTO.builder()
                .assyId(entity.getAssyId())
                .quantity(entity.getQuantity())
                .componentType(entity.getMaterial().getComponentType())
                .assyMaterialCode(entity.getAssemblyMaterial().getMaterialCode())
                .assyMaterialName(entity.getAssemblyMaterial().getMaterialName())
                .productCode(entity.getProduct().getProductCode())
                .productName(entity.getProduct().getProductName())
                .materialCode(entity.getMaterial().getMaterialCode())
                .materialName(entity.getMaterial().getMaterialName())
                .availableStock(availableStock)
                .stock(stock)
                .build();
    }

}
