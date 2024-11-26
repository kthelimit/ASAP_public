package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ExportDTO;

import java.util.List;

public interface ExportService {
    Long register(ExportDTO dto);

    Long modify(ExportDTO dto);

    List<ExportDTO> getCurrentExportList();

    Page<ExportDTO> getExports(Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInExportCode(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInProductionPlanCode(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInMaterialName(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInMaterialCode(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInProductName(String kerword, Pageable pageable);
}
