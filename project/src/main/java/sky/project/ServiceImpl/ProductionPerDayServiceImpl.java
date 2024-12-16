package sky.project.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.ProductionPerDayDTO;
import sky.project.Entity.ProductionPerDay;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.ProductionPerDayRepository;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Service.ProductionPerDayService;

@Service
@Slf4j
public class ProductionPerDayServiceImpl implements ProductionPerDayService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private ProductionPerDayRepository productionPerDayRepository;


    @Override
    public void register(ProductionPerDayDTO dto) {
        ProductionPerDay entity = dtoToEntity(dto);
        productionPerDayRepository.save(entity);
    }

    @Override
    public void delete(Long id){
        productionPlanRepository.deleteById(id);
    }


    private ProductionPerDay dtoToEntity(ProductionPerDayDTO dto) {

        ProductionPlan plan = productionPlanRepository.findByProductionPlanCode(dto.getProductionPlanCode());

        return ProductionPerDay.builder()
                .productionDate(dto.getProductionDate())
                .productionQuantity(dto.getProductionQuantity())
                .productionPlan(plan)
                .build();
    }
}
