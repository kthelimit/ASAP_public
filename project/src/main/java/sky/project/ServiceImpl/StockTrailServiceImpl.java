package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sky.project.Entity.StockTrail;
import sky.project.Repository.StockTrailRepository;
import sky.project.Service.StockTrailService;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockTrailServiceImpl implements StockTrailService {


    private final StockTrailRepository stockTrailRepository;

    @Override
    public void register(StockTrail entity){
        stockTrailRepository.save(entity);
    }
}
