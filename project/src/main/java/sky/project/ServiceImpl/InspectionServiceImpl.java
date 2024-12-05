package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.InspectionDTO;
import sky.project.Entity.*;
import sky.project.Repository.InspectionRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.OrderRepository;
import sky.project.Repository.SupplierRepository;
import sky.project.Service.InspectionService;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {
    private final MaterialRepository materialRepository;
    private final OrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final InspectionRepository inspectionRepository;

    @Override
    public Long register(InspectionDTO dto) {
        Inspection inspection = dtoToEntity(dto);
        int inspectionRound = inspectionRepository.countByOrderCode(dto.getOrderCode()) + 1;
        inspection.setInspectionRound(inspectionRound);
        inspectionRepository.save(inspection);
        return inspection.getInspectionId();
    }

    @Override
    public List<InspectionDTO> findByOrderCode(String orderCode) {
        return inspectionRepository.findByOrderCode(orderCode).stream().map(this::entityToDto).toList();
    }

    public Inspection dtoToEntity(InspectionDTO dto) {

        String materialCode = dto.getMaterialCode();
        if (materialCode == null || materialCode.isEmpty()) {
            materialCode = materialRepository.findCodeByMaterialName(dto.getMaterialName());
        }

        if (materialRepository.findByMaterialCode(materialCode).isPresent() && orderRepository.findByOrderCode(dto.getOrderCode()).isPresent()) {
            Material material = materialRepository.findByMaterialCode(materialCode).get();
            Order order = orderRepository.findByOrderCode(dto.getOrderCode()).get();
            Supplier supplier = supplierRepository.findBySupplierName(dto.getSupplierName());

            return Inspection.builder()
                    .inspectionId(dto.getInspectionId())
                    .inspectionCode(dto.getInspectionCode())
                    .inspectionDate(dto.getInspectionDate())
                    .material(material)
                    .order(order)
                    .supplier(supplier)
                    .status(CurrentStatus.ON_HOLD)
                    .build();
        } else {
            return null;
        }
    }

    public InspectionDTO entityToDto(Inspection inspection) {
        int remainingQuantity = 0;
        if (orderRepository.findByOrderCode(inspection.getOrder().getOrderCode()).isPresent()) {
            remainingQuantity = orderRepository.findByOrderCode(inspection.getOrder().getOrderCode()).get().getOrderQuantity();
            //만약 이미 종료된 검수 진행 차수가 이미 있다면 그것을 합쳐서 빼줄것

        }
        return InspectionDTO.builder()
                .inspectionId(inspection.getInspectionId())
                .inspectionCode(inspection.getInspectionCode())
                .orderCode(inspection.getOrder().getOrderCode())
                .materialName(inspection.getMaterial().getMaterialName())
                .materialCode(inspection.getMaterial().getMaterialCode())
                .supplierName(inspection.getSupplier().getSupplierName())
                .inspectionRound(inspection.getInspectionRound())
                .inspectionQuantity(inspection.getInspectionQuantity())
                .inspectionDate(inspection.getInspectionDate())
                .remainingQuantity(remainingQuantity)
                .status(inspection.getStatus().name())
                .build();
    }

}
