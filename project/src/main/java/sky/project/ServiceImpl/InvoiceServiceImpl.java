package sky.project.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.InvoiceDTO;
import sky.project.Entity.Invoice;
import sky.project.Entity.Material;
import sky.project.Repository.InvoiceRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Service.InvoiceService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        // DTO -> Entity 변환
        Invoice invoice = toEntity(invoiceDTO);

        // Invoice 저장
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Entity -> DTO 변환
        return toDTO(savedInvoice);
    }

    @Override
    public Page<InvoiceDTO> getAllInvoice(Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return invoices.map(this::toDTO);
    }

    @Override
    public Page<InvoiceDTO> findInvoicesBySupplierName(String supplierName, Pageable pageable) {
        return invoiceRepository.findBySupplierName(supplierName, pageable)
                .map(this::toDTO);
    }

    @Override
    public InvoiceDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        return toDTO(invoice);
    }

    @Override
    public List<InvoiceDTO> findInvoicesBySupplierName(String supplierName) {
        List<Invoice> invoices = findInvoicesBySupplier(supplierName);
        return invoices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Double> getYearlySummary(String supplierName) {
        List<Invoice> invoices = findInvoicesBySupplier(supplierName);
        return invoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getCreatedDate().getYear(),
                        Collectors.summingDouble(Invoice::getTotalPrice)
                ));
    }

    @Override
    public Map<String, Double> getMonthlySummary(String supplierName) {
        List<Invoice> invoices = findInvoicesBySupplier(supplierName);
        return invoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> String.format("%d-%02d",
                                invoice.getCreatedDate().getYear(),
                                invoice.getCreatedDate().getMonthValue()),
                        Collectors.summingDouble(Invoice::getTotalPrice)
                ));
    }

    @Override
    public double getTotalAmount(String supplierName) {
        List<Invoice> invoices = findInvoicesBySupplier(supplierName);
        return invoices.stream()
                .mapToDouble(Invoice::getTotalPrice)
                .sum();
    }

    // Helper 메서드
    private List<Invoice> findInvoicesBySupplier(String supplierName) {
        return invoiceRepository.findBySupplierName(supplierName);
    }

    // DTO -> Entity 변환
    private Invoice toEntity(InvoiceDTO dto) {
        if (dto == null) throw new IllegalArgumentException("InvoiceDTO cannot be null");

        Material material = materialRepository.findFirstByMaterialName(dto.getMaterialName())
                .orElseThrow(() -> new RuntimeException("Material not found for name: " + dto.getMaterialName()));

        Double unitPrice = material.getUnitPrice();
        if (unitPrice == null) throw new RuntimeException("Unit price is missing for material: " + dto.getMaterialName());

        Double calculatedTotalPrice = unitPrice * dto.getQuantity();
        return Invoice.builder()
                .invoiceId(dto.getInvoiceId())
                .supplierName(dto.getSupplierName())
                .materialName(dto.getMaterialName())
                .unitPrice(unitPrice)
                .quantity(dto.getQuantity())
                .totalPrice(calculatedTotalPrice)
                .build();
    }

    // Entity -> DTO 변환
    public InvoiceDTO toDTO(Invoice entity) {
        if (entity == null) return null;

        return InvoiceDTO.builder()
                .invoiceId(entity.getInvoiceId())
                .supplierName(entity.getSupplierName())
                .materialName(entity.getMaterialName())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .totalPrice(entity.getTotalPrice())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}
