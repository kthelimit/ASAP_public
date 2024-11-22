package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.AssyDTO;
import sky.project.Entity.Assy;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Repository.AssyRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.ProductRepository;
import sky.project.Service.AssyService;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AssyServiceImpl implements AssyService {

    private final MaterialRepository materialRepository;
    private final AssyRepository assyRepository;
    private final ProductRepository productRepository;

    @Override
    public Long register(AssyDTO dto) {
        Assy entity = dtoToEntity(dto);
        assyRepository.save(entity);
        return entity.getAssyId();
    }

    @Override
    public List<AssyDTO> findByAssyMaterialCode(String assyMaterialCode){
        List<Assy> assyList = assyRepository.findByAssyMaterialCode(assyMaterialCode);
        List<AssyDTO> assyDTOList = new ArrayList<>();
        assyList.forEach(assy -> {
            assyDTOList.add(entityToDto(assy));
        });
        return assyDTOList;
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
                .build();
    }

}
