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
        SupplierStock entity = null;
        log.info("dto: "+dto);
        if(supplierStockRepository.findById(dto.getSupplierStockId()).isPresent()){
            entity = supplierStockRepository.findById(dto.getSupplierStockId()).get();
            entity.setAvailableStock(dto.getAvailableStock());
            log.info("entity: "+entity);
        }
        else {
            entity = dtoToEntity(dto);
        }
        supplierStockRepository.save(entity);
        return entity.getSupplierStockId();
    }


    @Override
    public List<SupplierStockDTO> findBySupplierId(String supplierId){
        List<SupplierStock> supplierStocks = supplierStockRepository.findBySupplierId(supplierId);
        List<SupplierStockDTO> dtos = new ArrayList<>();
        supplierStocks.forEach(supplierStock -> {
            dtos.add(entityToDto(supplierStock));
        });
        return dtos;
    }

    public SupplierStockDTO entityToDto(SupplierStock entity) {

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
                .availableStock(entity.getAvailableStock())
                .build();

    }

    public SupplierStock dtoToEntity(SupplierStockDTO dto) {

        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent() && supplierRepository.findById(dto.getSupplierId()).isPresent()) {
            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            Supplier supplier = supplierRepository.findById(dto.getSupplierId()).get();

            return SupplierStock.builder()
                    .supplierStockId(dto.getSupplierStockId())
                    .availableStock(dto.getAvailableStock())
                    .supplier(supplier)
                    .material(material)
                    .build();
        } else return null;
    }

}
