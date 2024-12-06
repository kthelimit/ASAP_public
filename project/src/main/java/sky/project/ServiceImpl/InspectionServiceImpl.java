package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.InspectionDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.InspectionService;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {
    private final MaterialRepository materialRepository;
    private final OrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final InspectionRepository inspectionRepository;
    private final SupplierStockRepository supplierStockRepository;

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

    //협력사 페이지 출력용
    @Override
    public Page<InspectionDTO> findBySupplierName(String supplierName, Pageable pageable) {
        //완료된 것은 3일 전까지의 내용만 출력해줄 것.
        LocalDate limitDate = LocalDate.now().minusDays(3);
        return inspectionRepository.findBySupplierName(supplierName, limitDate, pageable).map(this::entityToDto);
    }

    //대시보드 출력용 남은 진척 검수 갯수
    @Override
    public int getCountInspectionForSupplier(String supplierName) {
        return inspectionRepository.countBySupplierOnHold(supplierName);
    }

    //대시보드 출력용 진척검수일체크
    @Override
    public boolean checkInspectionDate() {
        LocalDate today = LocalDate.now();
        List<Inspection> inspectionsForToday = inspectionRepository.findInspectionsByInspectionDate(today);
        return !inspectionsForToday.isEmpty();
    }


    //진척 검수를 완료할 때
    @Override
    public Long updateInspection(InspectionDTO dto) {
        //진척 검수 완료
        Inspection entity = inspectionRepository.findById(dto.getInspectionId()).orElse(null);
        assert entity != null;
        entity.setInspectionQuantity(dto.getInspectionQuantity() == null ? 0 : dto.getInspectionQuantity());
        entity.setStatus(CurrentStatus.FINISHED);
        inspectionRepository.save(entity);

        //진척 검수가 완료되면 해당 수량만큼 업체의 재고에 추가된다.
        SupplierStock stock = supplierStockRepository.findByMaterialCodeWithSupplierId(entity.getSupplier().getSupplierId(), entity.getMaterial().getMaterialCode());
        stock.setStock(stock.getStock() + entity.getInspectionQuantity());
        supplierStockRepository.save(stock);
        return entity.getInspectionId();
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
        String orderCode = inspection.getOrder().getOrderCode();
        if (orderRepository.findByOrderCode(orderCode).isPresent()) {
            remainingQuantity = orderRepository.findByOrderCode(orderCode).get().getOrderQuantity();
            int finishedQuantity = 0;
            //만약 이미 종료된 검수 진행 차수가 이미 있다면 그것을 합쳐서 빼줄것
            List<Inspection> list = inspectionRepository.countSumQuantityFromLastFinishedInspection(orderCode, inspection.getInspectionRound());
            if (!list.isEmpty()) {
                for (Inspection value : list) {
                    finishedQuantity += value.getInspectionQuantity();
                }
            }
            remainingQuantity -= finishedQuantity;
        }
        return InspectionDTO.builder()
                .inspectionId(inspection.getInspectionId())
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
