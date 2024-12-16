package sky.project.Service;

import sky.project.DTO.ProductionPerDayDTO;

import java.util.List;

public interface ProductionPerDayService {
    void register(ProductionPerDayDTO dto);

    void delete(Long id);

    List<ProductionPerDayDTO> findbyPlanId(Long planId);

    ProductionPerDayDTO findById(Long id);
}
