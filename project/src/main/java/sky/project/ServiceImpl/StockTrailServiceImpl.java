package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.GraphDTO;
import sky.project.DTO.StockTrailDTO;
import sky.project.Entity.Material;
import sky.project.Entity.StockTrail;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.StockTrailRepository;
import sky.project.Service.StockTrailService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockTrailServiceImpl implements StockTrailService {

    @Autowired
    StockTrailRepository stockTrailRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Override
    public void register(StockTrail entity) {
        stockTrailRepository.save(entity);
    }
    @Override
    public List<StockTrailDTO> findAllStockTrails() {
        return stockTrailRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, GraphDTO> getYearlyStockSummary() {
        List<StockTrail> stockTrails = stockTrailRepository.findAll();

        // 최신 5년 데이터만 필터링
        int currentYear = LocalDate.now().getYear();
        int fourYearsAgo = currentYear - 4;

        Map<String, StockTrail> latestStockTrails = stockTrails.stream()
                .filter(stockTrail -> stockTrail.getDate().getYear() >= fourYearsAgo) // 최신 5년 데이터만 필터링
                .collect(Collectors.toMap(
                        stockTrail -> stockTrail.getMaterial().getMaterialCode(),
                        Function.identity(),
                        (existing, replacement) -> existing.getDate().isAfter(replacement.getDate()) ? existing : replacement
                ));

        // 연도별 요약
        Map<String, GraphDTO> yearlySummary = new TreeMap<>(Comparator.reverseOrder()); // 역순 정렬
        for (StockTrail stockTrail : latestStockTrails.values()) {
            String key = String.format("%d년", stockTrail.getDate().getYear());

            GraphDTO graphDTO = yearlySummary.getOrDefault(key, new GraphDTO());
            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrail.getQuantity());
            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrail.getPrice());

            yearlySummary.put(key, graphDTO);
        }

        return yearlySummary;
    }


    @Override
    public Map<String, GraphDTO> getMonthlyStockSummary() {
        List<StockTrail> stockTrails = stockTrailRepository.findAll();

        // 현재 연도 가져오기
        int currentYear = LocalDate.now().getYear();

        // 최신 월들만 필터링
        Map<String, StockTrail> latestStockTrails = stockTrails.stream()
                .filter(stockTrail -> stockTrail.getDate().getYear() == currentYear)
                .collect(Collectors.toMap(
                        stockTrail -> stockTrail.getMaterial().getMaterialCode(),
                        Function.identity(),
                        (existing, replacement) -> existing.getDate().isAfter(replacement.getDate()) ? existing : replacement
                ));

        // 월별 요약
        Map<String, GraphDTO> monthlySummary = new TreeMap<>(Comparator.reverseOrder());
        for (StockTrail stockTrail : latestStockTrails.values()) {
            String key = String.format("%d년 %02d월",
                    stockTrail.getDate().getYear(),
                    stockTrail.getDate().getMonthValue());

            GraphDTO graphDTO = monthlySummary.getOrDefault(key, new GraphDTO());
            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrail.getQuantity());
            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrail.getPrice());

            monthlySummary.put(key, graphDTO);
        }

        return monthlySummary;
    }

    @Override
    public Map<String, GraphDTO> getWeeklyStockSummary() {
        List<StockTrail> stockTrails = stockTrailRepository.findAll();
        WeekFields weekFields = WeekFields.of(Locale.getDefault()); // 주 기준

        // 현재 연도 가져오기
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // material_code별 최신 데이터 필터링
        Map<String, StockTrail> latestStockTrails = stockTrails.stream()
                .filter(stockTrail -> stockTrail.getDate().getYear() == currentYear && stockTrail.getDate().getMonthValue() == currentMonth)
                .collect(Collectors.toMap(
                        stockTrail -> stockTrail.getMaterial().getMaterialCode(), // materialCode 접근
                        Function.identity(),
                        (existing, replacement) -> existing.getDate().isAfter(replacement.getDate()) ? existing : replacement
                ));

        // 주별 요약
        Map<String, GraphDTO> weeklySummary = new HashMap<>();
        for (StockTrail stockTrail : latestStockTrails.values()) {
            LocalDate date = LocalDate.from(stockTrail.getDate());
            int weekOfMonth = date.get(weekFields.weekOfMonth());
            String key = String.format("%s %d주차", date.format(DateTimeFormatter.ofPattern("yyyy년 MM월")), weekOfMonth);

            GraphDTO graphDTO = weeklySummary.getOrDefault(key, new GraphDTO());
            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrail.getQuantity());
            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrail.getPrice());

            weeklySummary.put(key, graphDTO);
        }

        return weeklySummary;
    }








//    @Override
//    public Map<String, GraphDTO> getYearlyStockSummary() {
//        List<StockTrail> stockTrails = stockTrailRepository.findAll();
//
//        Map<String, GraphDTO> yearlySummary = new HashMap<>();
//        for (int i = 0; i < stockTrails.size(); i++) {
//            String key = String.format("%d년", stockTrails.get(i).getDate().getYear());
//
//            GraphDTO graphDTO = yearlySummary.getOrDefault(key, new GraphDTO());
//            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrails.get(i).getQuantity()); //
//            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrails.get(i).getPrice()); //
//
//            yearlySummary.put(key, graphDTO);
//        }
//
//        return yearlySummary;
//    }
//
//    @Override
//    public Map<String, GraphDTO> getMonthlyStockSummary() {
//        List<StockTrail> stockTrails = stockTrailRepository.findAll();
//
//        Map<String, GraphDTO> monthlySummary = new HashMap<>();
//        for (int i = 0; i < stockTrails.size(); i++) {
//            String key = String.format("%d년 %02d월",
//                    stockTrails.get(i).getDate().getYear(),
//                    stockTrails.get(i).getDate().getMonthValue());
//
//            GraphDTO graphDTO = monthlySummary.getOrDefault(key, new GraphDTO());
//            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrails.get(i).getQuantity()); //
//            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrails.get(i).getPrice()); //
//
//            monthlySummary.put(key, graphDTO);
//        }
//
//        return monthlySummary;
//    }
//
//    @Override
//    public Map<String, GraphDTO> getWeeklyStockSummary() {
//        // StockTrail 데이터를 가져오는 쿼리
//        List<StockTrail> stockTrails = stockTrailRepository.findAll();
//        WeekFields weekFields = WeekFields.of(Locale.getDefault()); // 로케일에 따른 주 기준
//
//
//        Map<String, GraphDTO> weeklySummary = new HashMap<>();
//        for (int i = 0; i < stockTrails.size(); i++) {
//            // 날짜를 LocalDate로 변환
//            LocalDate date = LocalDate.from(stockTrails.get(i).getDate());
//            int weekOfMonth = date.get(weekFields.weekOfMonth());
//            String key = String.format("%s %d주차", date.format(DateTimeFormatter.ofPattern("yyyy년 MM월")), weekOfMonth);
//
//            // 기존 값 가져오기, 없으면 새로 생성
//            GraphDTO graphDTO = weeklySummary.getOrDefault(key, new GraphDTO());
//            graphDTO.setQuantity(graphDTO.getQuantity() + stockTrails.get(i).getQuantity());
//            graphDTO.setTotalPrice(graphDTO.getTotalPrice() + stockTrails.get(i).getPrice());
//
//            // 요약 맵에 다시 저장
//            weeklySummary.put(key, graphDTO);
//        }
//
//        return weeklySummary;
//    }


    // StockTrail 엔티티를 DTO로 변환
    public StockTrailDTO toDTO(StockTrail stockTrail) {
        return StockTrailDTO.builder()
                .id(stockTrail.getId())
                .materialCode(stockTrail.getMaterial().getMaterialCode()) // Material 코드
                .materialName(stockTrail.getMaterial().getMaterialName()) // Material 이름
                .quantity(stockTrail.getQuantity())
                .stock(stockTrail.getStock())
                .price(stockTrail.getPrice())  // 필요한 다른 값들도 넣어야 할 경우 추가
                .date(stockTrail.getDate())
                .build();
    }

    // StockTrailDTO를 엔티티로 변환
    public StockTrail toEntity(StockTrailDTO stockTrailDTO, String materialCode) {
        if (stockTrailDTO == null || materialCode == null || materialCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid StockTrailDTO or MaterialCode");
        }

        // materialCode를 이용해 Material을 조회
        Material material = materialRepository.findByMaterialCode(materialCode)
                .orElseThrow(() -> new RuntimeException("Material not found for code: " + materialCode));

        // StockTrail 엔티티로 변환
        return StockTrail.builder()
                .id(stockTrailDTO.getId())        // DTO에서 ID 사용 (필요 시)
                .material(material)               // Material 객체 연결
                .quantity(stockTrailDTO.getQuantity())
                .stock(stockTrailDTO.getStock())
                .price(stockTrailDTO.getPrice())
                .date(stockTrailDTO.getDate())
                .build();
    }


}
