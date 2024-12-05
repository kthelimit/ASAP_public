package sky.project.ServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ImportDTO;
import sky.project.DTO.InvoiceDTO;
import sky.project.Entity.Import;
import sky.project.Entity.Invoice;
import sky.project.Entity.Material;
import sky.project.Repository.InvoiceRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Service.InvoiceService;

@Service
public class InvoiceServiceImpl  implements InvoiceService {


    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    MaterialRepository materialRepository;


    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        // DTO를 Entity로 변환
        Invoice invoice = toEntity(invoiceDTO);

        // Invoice 엔티티 저장
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return toDTO(savedInvoice);
    }


    @Override
    public Page<InvoiceDTO> getAllInvoice(Pageable pageable) {

        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return invoices.map(invoice -> toDTO(invoice));
    }

    @Override
    public Page<InvoiceDTO> findInvoicesBySupplierName(String supplierName, Pageable pageable) {
        return invoiceRepository.findBySupplierName(supplierName, pageable)
                .map(invoice -> toDTO(invoice));
    }

    @Override
    public InvoiceDTO getInvoiceById(Long invoiceId) {
        // Invoice 엔티티 조회
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));

        // Entity -> DTO 변환
        return toDTO(invoice);
    }



    // DTO -> Entity 변환
    private Invoice toEntity(InvoiceDTO dto) {
        if (dto == null) throw new IllegalArgumentException("InvoiceDTO cannot be null");

        // MaterialRepository를 통해 Material 엔티티 조회
        Material material = materialRepository.findFirstByMaterialName(dto.getMaterialName())
                .orElseThrow(() -> new RuntimeException("Material not found for name: " + dto.getMaterialName()));

        // unitPrice 추출
        Double unitPrice = material.getUnitPrice();

        // totalPrice 계산
        Double calculatedTotalPrice = unitPrice * dto.getQuantity();

        return Invoice.builder()
                .invoiceId(dto.getInvoiceId()) // ID는 기존 값 사용
                .supplierName(dto.getSupplierName())
                .materialName(dto.getMaterialName())
                .unitPrice(unitPrice) // Material에서 조회된 단가 사용
                .quantity(dto.getQuantity())
                .totalPrice(calculatedTotalPrice) // 계산된 총 가격 설정
                .build();
    }

    // Entity -> DTO 변환
    public static InvoiceDTO toDTO(Invoice entity) {
        if (entity == null) return null;

        return InvoiceDTO.builder()
                .invoiceId(entity.getInvoiceId())
                .supplierName(entity.getSupplierName())
                .materialName(entity.getMaterialName())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .totalPrice(entity.getTotalPrice()) // 이미 계산된 값을 사용
                .createdDate(entity.getCreatedDate()) //
                .build();
    }
}


