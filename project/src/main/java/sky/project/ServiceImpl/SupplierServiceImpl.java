package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.project.DTO.SupplierDTO;
import sky.project.Entity.Supplier;
import sky.project.Entity.User;
import sky.project.Repository.SupplierRepository;
import sky.project.Repository.UserRepository;
import sky.project.Service.SupplierService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findByApprovedTrue(pageable);
    }
    public List<Supplier> getApprovedSuppliers() {
        return supplierRepository.findByApprovedTrue();
    }

    @Override
    public SupplierDTO getSupplierById(String supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + supplierId));

        return SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .userId(supplier.getUser().getUserId()) // User 엔티티에서 userId 가져옴
                .businessRegistrationNumber(supplier.getBusinessRegistrationNumber())
                .supplierName(supplier.getSupplierName())
                .contactInfo(supplier.getContactInfo())
                .address(supplier.getAddress())
                .businessType(supplier.getBusinessType())
                .businessItem(supplier.getBusinessItem())
                .approved(supplier.getApproved())
                .build();
    }


    @Override
    public void registerSupplier(SupplierDTO supplierDTO) {
        User user = userRepository.findById(supplierDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + supplierDTO.getUserId()));

        Supplier supplier = Supplier.builder()
                .supplierId(supplierDTO.getSupplierId())
                .user(user)
                .businessRegistrationNumber(supplierDTO.getBusinessRegistrationNumber())
                .supplierName(supplierDTO.getSupplierName())
                .contactInfo(supplierDTO.getContactInfo())
                .address(supplierDTO.getAddress())
                .businessType(supplierDTO.getBusinessType())
                .businessItem(supplierDTO.getBusinessItem())
                .approved(false)
                .contractFilePath(supplierDTO.getContractFilePath())
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
        return supplierRepository.countByApprovedFalse();
    }

    @Transactional
    @Override
    public void approveSupplier(String supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NoSuchElementException("No supplier found with ID: " + supplierId));
        supplier.setApproved(true);
        supplierRepository.save(supplier);
        System.out.println("Supplier approved with ID: " + supplierId);
    }

    @Transactional
    @Override
    public void rejectSupplier(String supplierId) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NoSuchElementException("No supplier found with ID: " + supplierId));
        supplierRepository.delete(supplier);

        System.out.println("Supplier rejected and deleted with ID: " + supplierId);
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
                .businessItem(supplier.getBusinessItem())
                .approved(supplier.getApproved())
                .contractFilePath(supplier.getContractFilePath())
                .build();
    }




}
