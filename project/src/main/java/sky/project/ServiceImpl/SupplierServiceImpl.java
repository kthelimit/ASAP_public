package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.SupplierDTO;
import sky.project.Entity.Supplier;
import sky.project.Repository.SupplierRepository;
import sky.project.Repository.UserRepository;
import sky.project.Service.SupplierService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Override
    public void registerSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = Supplier.builder()
                .user(userRepository.findByUserId(supplierDTO.getUserId()).orElseThrow())
                .supplierName(supplierDTO.getSupplierName())
                .businessRegistrationNumber(supplierDTO.getBusinessRegistrationNumber())
                .contactInfo(supplierDTO.getContactInfo())
                .address(supplierDTO.getAddress())
                .businessType(supplierDTO.getBusinessType())
                .approved(false)
                .build();
        supplierRepository.save(supplier);
    }

    @Override
    public boolean isAlreadyRegistered(String userId) {
        return supplierRepository.existsByUserUserId(userId);
    }

    @Override
    public List<SupplierDTO> getPendingApprovals(int page, int size) {
        Page<Supplier> supplierPage = supplierRepository.findByApprovedFalse(PageRequest.of(page - 1, size));
        return supplierPage.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public long getTotalPendingCount() {
        // 승인되지 않은 공급자의 총 개수를 반환
        return supplierRepository.countByApprovedFalse();
    }

    @Override
    public void approveSupplier(String supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow();
        supplier.setApproved(true);
        supplier.getUser().setUserType("SUPPLIER");
        supplierRepository.save(supplier);
    }

    @Override
    public void rejectSupplier(String supplierId) {
        supplierRepository.deleteById(supplierId);
    }

    private SupplierDTO toDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .userId(supplier.getUser().getUserId())
                .businessRegistrationNumber(supplier.getBusinessRegistrationNumber())
                .supplierName(supplier.getSupplierName())
                .contactInfo(supplier.getContactInfo())
                .address(supplier.getAddress())
                .businessType(supplier.getBusinessType())
                .approved(supplier.getApproved())
                .build();
    }
    @Override
    public String getSupplierNameByUserId(String userId) {
        return supplierRepository.findByUser_UserId(userId)
                .map(supplier -> supplier.getSupplierName())
                .orElse(null); // supplierName 반환 또는 null 반환
    }
}
