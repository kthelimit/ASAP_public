package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Service.ProductionPlanService;

@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public Page<ProductionPlanDTO> getProductionPlans(Pageable pageable) {
        return productionPlanRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<ProductionPlanDTO> searchProductionPlans(String keyword, Pageable pageable) {
        Page<ProductionPlan> productionPlansPage = productionPlanRepository.findByProductNameContaining(keyword, pageable);
        return productionPlansPage.map(this::toDTO);
    }

    @Override
    public void registerProductionPlan(ProductionPlanDTO productionPlanDTO) {
        ProductionPlan plan = toEntity(productionPlanDTO);
        productionPlanRepository.save(plan);
    }

    @Override
    public ProductionPlanDTO getProductionPlanById(Long id) {
        ProductionPlan plan = productionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + id));
        return toDTO(plan);
    }

    @Override
    public void updateProductionPlan(ProductionPlanDTO productionPlanDTO) {
        ProductionPlan plan = productionPlanRepository.findById(productionPlanDTO.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + productionPlanDTO.getPlanId()));

        plan.setProductionStartDate(productionPlanDTO.getProductionStartDate());
        plan.setProductionEndDate(productionPlanDTO.getProductionEndDate());
        plan.setProductCode(productionPlanDTO.getProductCode());
        plan.setProductName(productionPlanDTO.getProductName());
        plan.setProductionQuantity(productionPlanDTO.getProductionQuantity());

        productionPlanRepository.save(plan);
    }

    @Override
    public void deleteProductionPlan(Long id) {
        if (!productionPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + id);
        }
        productionPlanRepository.deleteById(id);
    }



    private ProductionPlanDTO toDTO(ProductionPlan plan) {
        if (plan == null) return null;

        ProductionPlanDTO dto = new ProductionPlanDTO();
        dto.setPlanId(plan.getPlanId());
        dto.setProductCode(plan.getProductCode());
        dto.setProductName(plan.getProductName());
        dto.setProductionStartDate(plan.getProductionStartDate());
        dto.setProductionEndDate(plan.getProductionEndDate());
        dto.setProductionQuantity(plan.getProductionQuantity());
        return dto;
    }



    private ProductionPlan toEntity(ProductionPlanDTO dto) {
        if (dto == null) return null;

        ProductionPlan plan = new ProductionPlan();
        plan.setPlanId(dto.getPlanId());
        plan.setProductCode(dto.getProductCode());
        plan.setProductName(dto.getProductName());
        plan.setProductionStartDate(dto.getProductionStartDate());
        plan.setProductionEndDate(dto.getProductionEndDate());
        plan.setProductionQuantity(dto.getProductionQuantity());
        return plan;
    }
}