package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ExportDTO;

import java.util.List;

public interface ExportService {
    Long register(ExportDTO dto);

    Long modify(ExportDTO dto);

    Long modifyFinished(ExportDTO dto);

    List<ExportDTO> getCurrentExportList();

    Page<ExportDTO> getCurrentExportListPage(Pageable pageable);

    Page<ExportDTO> getExports(Pageable pageable);

    Page<ExportDTO> getExportsNotHOLD(Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInExportCode(String kerword, Pageable pageable);

    Page<ExportDTO> getCurrentExportsWithSearchInExportCode(String keyword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInProductionPlanCode(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInMaterialName(String kerword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInMaterialCode(String kerword, Pageable pageable);

    Page<ExportDTO> getCurrentExportsWithSearchInProductionPlanCode(String keyword, Pageable pageable);

    Page<ExportDTO> getCurrentExportsWithSearchInMaterialName(String keyword, Pageable pageable);

    Page<ExportDTO> getCurrentExportsWithSearchInMaterialCode(String keyword, Pageable pageable);

    Page<ExportDTO> getExportsWithSearchInProductName(String kerword, Pageable pageable);

    Page<ExportDTO> getCurrentExportsWithSearchInProductName(String keyword, Pageable pageable);

    int findSumByProductionPlanCodeAndMaterialCode(String productionPlanCode, String materialCode);

    //대시보드 출력용 출고 요청 건수
    int getCountCurrentRequest();

    //대시 보드 출력용 승인된 출고 요청 건수
    int getCountApprovedRequestThisMonth();

    //대시 보드 출력용 불출 완료된 출고 요청 건수
    int getCountFinishedRequestThisMonth();

    //대시보드 출력용 최근 출고요청 리스트
    List<ExportDTO> getRecentExportList();
}
