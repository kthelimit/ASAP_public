package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.StockDTO;
import sky.project.Entity.Export;
import sky.project.Entity.Material;
import sky.project.Entity.Stock;
import sky.project.Repository.ExportRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.StockRepository;
import sky.project.Service.StockService;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MaterialRepository materialRepository;
    private final ExportRepository exportRepository;

    @Override
    public Long register(StockDTO dto) {
        Stock entity = null;
        if (stockRepository.findByMaterialCode(dto.getMaterialCode()) != null) {
            entity = stockRepository.findByMaterialCode(dto.getMaterialCode());
            entity.setQuantity(dto.getQuantity());

            //가용재고 처리(고민중) 현재는 기초 재고로 받은 것을 전부 가용재고로 갖고 있다.
            entity.setAvailableStock(dto.getQuantity());
        } else {
            entity = dtoToEntity(dto);

        }
        stockRepository.save(entity);
        return entity.getStockId();
    }

    //목록 불러오기
    @Override
    public Page<StockDTO> getStocks(Pageable pageable) {

        return stockRepository.findAll(pageable).map(this::entityToDto);
    }

    //검색
    @Override
    public Page<StockDTO> getStocksWithSearchInMaterialName(String keyword, Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByMaterialName(keyword, pageable);
        return stockPage.map(this::entityToDto);
    }

    @Override
    public Page<StockDTO> getStocksWithSearchInMaterialType(String keyword, Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByMaterialType(keyword, pageable);
        return stockPage.map(this::entityToDto);
    }

    @Override
    public Page<StockDTO> getStocksWithSearchInMaterialCode(String keyword, Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByMaterialCode(keyword, pageable);
        return stockPage.map(this::entityToDto);
    }

    @Override
    public Page<StockDTO> getStocksWithSearchInComponentType(String keyword, Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findByComponentType(keyword, pageable);
        return stockPage.map(this::entityToDto);
    }


    public Stock dtoToEntity(StockDTO dto) {

        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();

            return Stock.builder()
                    .stockId(dto.getStockId())
                    .material(material)
                    .quantity(dto.getQuantity())
                    //가용재고 처리(고민중) 현재는 기초 재고로 받은 것을 전부 가용재고로 갖고 있다.
                    .availableStock(dto.getQuantity())
                    .build();
        } else return null;

    }

    public StockDTO entityToDto(Stock stock) {
        int availableStock = calculateAvailableStock(stock);
        stockRepository.save(stock);
        return StockDTO.builder()
                .stockId(stock.getStockId())
                .quantity(stock.getQuantity())
                .availableStock(availableStock)
                .materialCode(stock.getMaterial().getMaterialCode())
                .materialName(stock.getMaterial().getMaterialName())
                .materialType(stock.getMaterial().getMaterialType())
                .componentType(stock.getMaterial().getComponentType())
                .unitPrice(stock.getMaterial().getUnitPrice())
                .build();
    }

    @Override
    public int calculateAvailableStock(Stock stock){
        int availableStock = stock.getQuantity();
        if (exportRepository.findByMaterialCodeAndStatusOnHold(stock.getMaterial().getMaterialCode()) != null) {
            List<Export> exports = exportRepository.findByMaterialCodeAndStatusOnHold(stock.getMaterial().getMaterialCode());
            int exportQuantity = 0;
            for (Export export : exports) {
                exportQuantity += export.getRequiredQuantity();
            }
            availableStock -= exportQuantity;
        }
        return availableStock;
    }
}
