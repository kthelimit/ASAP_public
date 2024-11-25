package sky.project.Service;

import sky.project.DTO.ExportDTO;

import java.util.List;

public interface ExportService {
    Long register(ExportDTO dto);

    List<ExportDTO> getCurrentExportList();
}
