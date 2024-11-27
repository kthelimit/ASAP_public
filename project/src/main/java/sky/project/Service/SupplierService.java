package sky.project.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.SupplierDTO;
import sky.project.Entity.Supplier;

import java.util.List;

public interface SupplierService {
    Page<SupplierDTO> getAllSuppliers(Pageable pageable);

    List<Supplier> getApprovedSuppliers();

    void registerSupplier(SupplierDTO supplierDTO);  // 불필요한 오버로딩 메서드 제거

    List<SupplierDTO> findSuppliersByMaterialCode(String materialCode);

    boolean isAlreadyRegistered(String userId);

    List<SupplierDTO> getPendingApprovals(int page, int size);

    long getTotalPendingCount();

    void approveSupplier(String supplierId);

    void rejectSupplier(String supplierId);

    SupplierDTO getSupplierById(String id);


    Page<SupplierDTO> searchSuppliers(String keyword, Pageable pageable);

    Supplier getSupplierByName(String supplierName);
    String findSupplierNameByUserId(String userId);

    int getCountSupplierWhoWaitApproval();
}
