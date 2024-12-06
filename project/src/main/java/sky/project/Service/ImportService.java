package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.ImportDTO;
import sky.project.Entity.CurrentStatus;

import java.util.List;

public interface ImportService {
    // 입고 목록 조회
    List<ImportDTO> getAllImports();

    // 입고 정보 등록
    ImportDTO createImport(ImportDTO importDTO);

    // 입고 정보 수정
    ImportDTO updateImport(Long importId, ImportDTO importDTO);

    // 입고 정보 삭제
    void deleteImport(Long importId);

    // 특정 입고 정보 조회
    ImportDTO getImportById(Long importId);

    Page<ImportDTO> getImportsByCriteria(String type, String keyword, Pageable pageable);

    void updateImportStatus(Long importId, CurrentStatus status, Integer passedQuantity);

    List<ImportDTO> calculateDefectiveQuantity(List<ImportDTO> importList);
}