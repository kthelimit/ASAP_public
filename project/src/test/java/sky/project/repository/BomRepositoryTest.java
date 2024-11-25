package sky.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.project.DTO.ExportDTO;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.BomRepository;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Service.ExportService;

@SpringBootTest
public class BomRepositoryTest {
    @Autowired
    private BomRepository repository;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Test
    public void testFindByProductionPlanCode() {

        ProductionPlan entity =  productionPlanRepository.findByProductionPlanCode("PDPBA2411001");
        System.out.println(entity);
    }

    @Test
    public void registerExportTest(){

        ExportDTO exportDTO = ExportDTO.builder()
                .productionPlanCode("PDPBA2411001")
                .materialCode("MATRIMAT003")
                .requiredQuantity(10)
                .build();


        exportService.register(exportDTO);
    }
}
