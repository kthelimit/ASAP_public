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
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByApprovedTrue(pageable);
        return suppliers.map(this::toDTO); // Supplier -> SupplierDTO 변환
    }

    public List<Supplier> getApprovedSuppliers() {
        return supplierRepository.findByApprovedTrue();
    }


    @Override
    public Page<SupplierDTO> searchSuppliers(String keyword, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findBySupplierNameContaining(keyword, pageable);
        return suppliers.map(this::toDTO);
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
    public List<SupplierDTO> findSuppliersByMaterialCode(String materialCode) {
        // 자재 코드와 관련된 공급업체 조회 로직 구현
        List<Supplier> suppliers = supplierRepository.findSuppliersByMaterialCode(materialCode);
        return suppliers.stream().map(supplier -> SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .contactInfo(supplier.getContactInfo())
                .build()).collect(Collectors.toList());
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

    @Override
    public Supplier getSupplierByName(String supplierName) {
        Supplier supplier = supplierRepository.findBySupplierName(supplierName);
        if (supplier == null) {
            throw new RuntimeException("Supplier not found with name: " + supplierName);
        }
        return supplier;
    }
    @Override
    public String findSupplierNameByUserId(String userId) {
        Supplier supplier = supplierRepository.findByUser_UserId(userId);
        return supplier != null ? supplier.getSupplierName() : null;
    }

    //승인 대기중인 업체 수
    @Override
    public int getCountSupplierWhoWaitApproval() {
        return supplierRepository.countByApprovedYet();
    }


}
