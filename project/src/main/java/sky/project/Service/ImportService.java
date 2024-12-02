package sky.project.Service;

import sky.project.DTO.ImportDTO;

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
}