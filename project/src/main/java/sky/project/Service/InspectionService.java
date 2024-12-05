package sky.project.Service;

import sky.project.DTO.InspectionDTO;

import java.util.List;

public interface InspectionService {
    Long register(InspectionDTO dto);

    List<InspectionDTO> findByOrderCode(String orderCode);

    List<InspectionDTO> findBySupplierName(String supplierName);

    int getCountInspectionForSupplier(String supplierName);

    boolean checkInspectionDate();

    Long updateInspection(InspectionDTO dto);
}
