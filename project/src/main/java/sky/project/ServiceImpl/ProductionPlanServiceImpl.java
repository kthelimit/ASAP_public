package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Service.ProductionPlanService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        // 생산 계획 코드 생성 로직 추가
        String productionPlanCode = generateProductionPlanCode(productionPlanDTO);
        plan.setProductionPlanCode(productionPlanCode);

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

        // 기존 생산 계획 코드는 수정하지 않음
        plan.setProductionStartDate(productionPlanDTO.getProductionStartDate());
        plan.setProductionEndDate(productionPlanDTO.getProductionEndDate());
        plan.setProductionPlanCode(productionPlanDTO.getProductionPlanCode());
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
        dto.setProductionPlanCode(plan.getProductionPlanCode()); // 코드 추가
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

    private String generateProductionPlanCode(ProductionPlanDTO dto) {
        // 제품명에 따른 접두어 설정
        String prefix;
        switch (dto.getProductName()) {
            case "전기자전거A":
                prefix = "PDPBA";
                break;
            case "전기자전거B":
                prefix = "PDPBB";
                break;
            case "전동킥보드":
                prefix = "PDPBK";
                break;
            default:
                prefix = "PDPUN";
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        String dateCode = dto.getProductionStartDate().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = productionPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }

    @Override
    public List<ProductionPlanDTO> getProductionPlansWithDate() {

        LocalDate today = LocalDate.now();
        List<ProductionPlan> productionPlans = productionPlanRepository.findByPlanDate(today);
        List<ProductionPlanDTO> dtos = new ArrayList<>();
        for (ProductionPlan productionPlan : productionPlans) {
            dtos.add(toDTO(productionPlan));
        }
        return dtos;
    }


    //대시보드의 생산 계획 수
    @Override
    public int getCountProductionPlan() {
        LocalDate today = LocalDate.now();
        return productionPlanRepository.countCurrentPlan(today);
    }
}
