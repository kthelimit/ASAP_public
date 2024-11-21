package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sky.project.DTO.StockDTO;
import sky.project.Entity.Material;
import sky.project.Entity.Stock;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.StockRepository;
import sky.project.Service.StockService;

@Service
@Log4j2
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MaterialRepository materialRepository;

    @Override
    public Long register(StockDTO dto) {

        Stock entity = dtoToEntity(dto);
        stockRepository.save(entity);
        return entity.getStockId();
    }

    @Override
    public Page<StockDTO> getStocks(PageRequest pageRequest){
        return stockRepository.findAll(pageRequest).map(this::entityToDto);
    }




    public Stock dtoToEntity(StockDTO dto) {

        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            Stock stock = Stock.builder()
                    .stockId(dto.getStockId())
                    .material(material)
                    .quantity(dto.getQuantity())
                    .availableStock(dto.getAvailableStock())
                    .build();
            return stock;
        } else return null;

    }

    public StockDTO entityToDto(Stock stock) {

        return StockDTO.builder()
                .stockId(stock.getStockId())
                .quantity(stock.getQuantity())
                .availableStock(stock.getAvailableStock())
                .materialCode(stock.getMaterial().getMaterialCode())
                .materialName(stock.getMaterial().getMaterialName())
                .unitPrice(stock.getMaterial().getUnitPrice())
                .build();
    }
}
