package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.InspectionDTO;

import java.util.List;

public interface InspectionService {
    Long register(InspectionDTO dto);

    List<InspectionDTO> findByOrderCode(String orderCode);

    //협력사 페이지 출력용
    Page<InspectionDTO> findBySupplierName(String supplierName, Pageable pageable);

    int getCountInspectionForSupplier(String supplierName);

    boolean checkInspectionDate();

    Long updateInspection(InspectionDTO dto);
}
