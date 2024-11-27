package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.SupplierStockDTO;
import sky.project.Entity.Material;
import sky.project.Entity.Supplier;
import sky.project.Entity.SupplierStock;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.SupplierRepository;
import sky.project.Repository.SupplierStockRepository;
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

    @Override
    public Long register(SupplierStockDTO dto) {
        SupplierStock entity = dtoToEntity(dto);
        supplierStockRepository.save(entity);
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
    public List<SupplierStockDTO> findBySupplierId(String supplierId) {
        List<SupplierStock> supplierStocks = supplierStockRepository.findBySupplierId(supplierId);
        List<SupplierStockDTO> dtos = new ArrayList<>();
        supplierStocks.forEach(supplierStock -> {
            dtos.add(entityToDto(supplierStock));
        });
        return dtos;
    }

    public SupplierStockDTO entityToDto(SupplierStock entity) {

        //가용재고 계산
//        int availableStock = entity.getStock()- (발주요청 수량 다 더한거(서플라이어가 같고, 자재가 같아야하고, 아직 대기중이어야함));

        return SupplierStockDTO.builder()
                .supplierStockId(entity.getSupplierStockId())
                .supplierName(entity.getSupplier().getSupplierName())
                .supplierId(entity.getSupplier().getSupplierId())
                .materialType(entity.getMaterial().getMaterialType())
                .materialCode(entity.getMaterial().getMaterialCode())
                .materialName(entity.getMaterial().getMaterialName())
                .quantity(entity.getMaterial().getQuantity())
                .componentType(entity.getMaterial().getComponentType())
                .unitPrice(entity.getMaterial().getUnitPrice())
                .stock(entity.getStock())
                .availableStock(entity.getStock())
//                .availableStock(availableStock)
                .build();

    }

    public SupplierStock dtoToEntity(SupplierStockDTO dto) {

        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent() && supplierRepository.findById(dto.getSupplierId()).isPresent()) {
            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            Supplier supplier = supplierRepository.findById(dto.getSupplierId()).get();

            return SupplierStock.builder()
                    .supplierStockId(dto.getSupplierStockId())
                    .stock(dto.getStock())
                    .supplier(supplier)
                    .material(material)
                    .build();
        } else return null;
    }

}
