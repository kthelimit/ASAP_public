package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Entity.ProductionPerDay;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.ProductionPerDayRepository;
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
    @Autowired
    private ProductionPerDayRepository productionPerDayRepository;

    @Override
    public Page<ProductionPlanDTO> getProductionPlans(Pageable pageable) {
        return productionPlanRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public List<ProductionPlan> getProductionPlans() {
        return productionPlanRepository.findAll();
    }

    @Override
    public Page<ProductionPlanDTO> searchProductionPlans(String keyword, Pageable pageable) {
        Page<ProductionPlan> productionPlansPage = productionPlanRepository.findByProductNameContaining(keyword, pageable);
        return productionPlansPage.map(this::toDTO);
    }

    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO) {
        ProductionPlan plan = toEntity(productionPlanDTO);

        //만약 생산계획 코드가 없는 경우(새로 입력된 것인 경우)
        if (productionPlanDTO.getProductionPlanCode() == null || productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getProductionPlanCode()) == null) {
            // 생산 계획 코드 생성 로직 추가
            String productionPlanCode = generateProductionPlanCode(productionPlanDTO);
            plan.setProductionPlanCode(productionPlanCode);
        } else { //생산 계획 코드가 있는 경우 덮어쓰기한다.
            plan = productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getProductionPlanCode());
            plan.setProductName(productionPlanDTO.getProductName());
            plan.setProductCode(productionPlanDTO.getProductCode());
            plan.setProductionQuantity(productionPlanDTO.getProductionQuantity());
            plan.setProductionStartDate(productionPlanDTO.getProductionStartDate());
            plan.setProductionEndDate(productionPlanDTO.getProductionEndDate());
        }
        productionPlanRepository.save(plan);

        return plan.getProductionPlanCode();
    }

    @Override
    public ProductionPlanDTO getProductionPlanById(Long id) {
        ProductionPlan plan = productionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + id));
        return toDTO(plan);
    }

    @Override
    public String updateProductionPlan(ProductionPlanDTO productionPlanDTO) {
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
        return plan.getProductionPlanCode();
    }

    @Override
    public void deleteProductionPlan(Long id) {
        if (!productionPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 생산 계획을 찾을 수 없습니다: " + id);
        }

        //먼저 해당 생산 계획으로 등록된 일자별 생산 내용을 지워야함
        List<ProductionPerDay> perDays = productionPerDayRepository.findByProductionId(id);
        productionPerDayRepository.deleteAll(perDays);

        //삭제
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

        //내일 생산할 물건들을 오늘 요청할 수 있다.
        LocalDate date = LocalDate.now().plusDays(1);
        List<ProductionPlan> productionPlans = productionPlanRepository.findByPlanDate(date);
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
