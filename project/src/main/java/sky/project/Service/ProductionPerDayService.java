package sky.project.Service;

import sky.project.DTO.ProductionPerDayDTO;

public interface ProductionPerDayService {
    void register(ProductionPerDayDTO dto);

    void delete(Long id);
}
