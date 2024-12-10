package sky.project.Service;

import sky.project.DTO.ReturnDTO;

import java.util.List;

public interface ReturnService {
    Long register(ReturnDTO dto);

    void returnUpdate(Long returnId);

    ReturnDTO findByImportId(Long importId);

    List<ReturnDTO> findBySupplierNameOnHOLD(String supplierName);
}
