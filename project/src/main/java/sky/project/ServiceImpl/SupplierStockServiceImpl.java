package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.SupplierStockDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.SupplierStockService;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class SupplierStockServiceImpl implements SupplierStockService {

    private final SupplierStockRepository supplierStockRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRequestRepository deliveryRequestRepository;


    @Override
    public Long register(SupplierStockDTO dto) {
        SupplierStock entity;
        //공급업체와 자재로 중복이 있는지 검사
        if (supplierStockRepository.findBySupplierNameAndMaterialCode(dto.getSupplierName(), dto.getMaterialCode()) != null) {
            entity = supplierStockRepository.findBySupplierNameAndMaterialCode(dto.getSupplierName(), dto.getMaterialCode());
            entity.setStock(dto.getStock());
        } else {
            entity = dtoToEntity(dto);
            supplierStockRepository.save(entity);
        }
        return entity.getSupplierStockId();
    }

    @Override
    public void updateStock(SupplierStockDTO dto) {
        if (supplierStockRepository.findById(dto.getSupplierStockId()).isPresent()) {
            SupplierStock entity = supplierStockRepository.findById(dto.getSupplierStockId()).get();
            entity.setStock(dto.getStock());
            supplierStockRepository.save(entity);
        }
    }

    @Override
    public List<SupplierStock> getStocks() {
        return supplierStockRepository.findAll();
    }


    @Override
    public List<SupplierStockDTO> findBySupplierId(String supplierId) {
        List<SupplierStock> supplierStocks = supplierStockRepository.findBySupplierId(supplierId);
        List<SupplierStockDTO> dtos = new ArrayList<>();
        supplierStocks.forEach(supplierStock -> {
            dtos.add(entityToDto(supplierStock));
        });
        return dtos;
    }

    public SupplierStockDTO entityToDto(SupplierStock entity) {
        // Entity에서 필요한 데이터 추출
        String supplierName = entity.getSupplier().getSupplierName();
        String materialName = entity.getMaterial().getMaterialName();

        // 해당 공급사의 자재 재고 조회
        int stock = supplierStockRepository.findBySupplier_SupplierNameAndMaterial_MaterialName(supplierName, materialName)
                .map(SupplierStock::getStock)
                .orElse(0);

        List<CurrentStatus> statuses = List.of(CurrentStatus.APPROVAL, CurrentStatus.IN_PROGRESS, CurrentStatus.FINISHED);

        // APPROVAL 상태인 발주 요청 수량 조회
//        int approvedQuantity = orderRepository.findApprovedQuantity(supplierName, materialName, statuses);

        //남은 발주 수량 조회
        List<Order> orders = orderRepository.findBySupplierName(supplierName);
        int totalRemainedQuantity = 0;
        for (Order order : orders) {

            // 남은 조달 수량 ( 발주량 - 현재 납품지시 넣은 수량 + 불량이라 판정된 수량)
            int remainedQuantity = order.getOrderQuantity();
            List<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findDeliveryRequestsByOrderCode(order.getOrderCode());
            int sumOfRequests = 0;
            for (DeliveryRequest deliveryRequest : deliveryRequests) {
                sumOfRequests += deliveryRequest.getRequestedQuantity();
            }
            remainedQuantity -= sumOfRequests;
            totalRemainedQuantity += remainedQuantity;

        }

        // 가용 재고 계산
//        int availableStock = stock - approvedQuantity;
        int availableStock = stock - totalRemainedQuantity;

        return SupplierStockDTO.builder()
                .supplierStockId(entity.getSupplierStockId())
                .supplierName(supplierName)
                .supplierId(entity.getSupplier().getSupplierId())
                .materialType(entity.getMaterial().getMaterialType())
                .materialCode(entity.getMaterial().getMaterialCode())
                .materialName(materialName)
                .quantity(entity.getMaterial().getQuantity())
                .componentType(entity.getMaterial().getComponentType())
                .unitPrice(entity.getMaterial().getUnitPrice())
                .stock(stock) // 계산된 재고
                .availableStock(availableStock) // 가용 재고
                .build();
    }

    public SupplierStock dtoToEntity(SupplierStockDTO dto) {

        String supplierId = dto.getSupplierId();
        if (supplierId == null) {
            supplierId = supplierRepository.findSupplierIdBySupplierName(dto.getSupplierName());
        }

        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent() && supplierRepository.findById(supplierId).isPresent()) {
            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();

            Supplier supplier = supplierRepository.findById(supplierId).get();

            return SupplierStock.builder()
                    .supplierStockId(dto.getSupplierStockId())
                    .stock(dto.getStock())
                    .supplier(supplier)
                    .material(material)
                    .build();
        } else return null;
    }

}
