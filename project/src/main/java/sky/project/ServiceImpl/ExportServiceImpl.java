package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.project.DTO.ExportDTO;
import sky.project.Entity.*;
import sky.project.Repository.ExportRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Repository.StockRepository;
import sky.project.Service.ExportService;
import sky.project.Service.StockTrailService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final MaterialRepository materialRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ExportRepository exportRepository;
    private final StockRepository stockRepository;
    private final StockTrailService stockTrailService;

    @Override
    public Long register(ExportDTO dto) {
        log.info("register dto: " + dto);
        log.info(dto.getMaterialCode());
        log.info(dto.getProductionPlanCode());
        log.info(dto.getAssyMaterialCode());
        Export entity = dtoToEntity(dto);
        entity.setExportCode(generateProductionPlanCode(dto));
        exportRepository.save(entity);
        return entity.getExportId();
    }

    //출고 요청 승인
    @Override
    public Long modify(ExportDTO dto) {
        // Export 객체를 찾고 상태 변경
        Export export = exportRepository.findByExportCode(dto.getExportCode());
        log.info(export);

        // 상태 전환
        export.setExportStatus(CurrentStatus.APPROVAL);
        exportRepository.save(export);

        // Stock 객체를 찾고, 해당 물품의 Material 객체에서 unitPrice 가져오기
        Stock stock = stockRepository.findByMaterialCode(dto.getMaterialCode());
        double unitPrice = stock.getMaterial().getUnitPrice(); // Material 엔티티에서 unitPrice 가져오기

        // 재고에서 수량 차감
        stock.setQuantity(stock.getQuantity() - export.getRequiredQuantity());
        stockRepository.save(stock);

        // 최종 재고 금액 계산: 재고 수량 * 유닛 가격
        double finalPrice = stock.getQuantity() * unitPrice;

        // 추적용 StockTrail 생성
        StockTrail stockTrail = StockTrail.builder()
                .material(stock.getMaterial())  // Material 정보 포함
                .quantity(-export.getRequiredQuantity())  // 분량만큼 차감
                .price(finalPrice)  // 누적된 최종 가격
                .stock(stock.getQuantity())  // 남은 재고 수량
                .date(LocalDateTime.now())  // 현재 시간
                .build();

        // StockTrail 서비스에 등록
        stockTrailService.register(stockTrail);

        return export.getExportId();
    }






    //불출 완료
    @Override
    public Long modifyFinished(ExportDTO dto) {
        Export export = exportRepository.findByExportCode(dto.getExportCode());
        log.info(export);
        //상태 전환
        export.setExportStatus(CurrentStatus.FINISHED);
        exportRepository.save(export);

        return export.getExportId();
    }

    public Export dtoToEntity(ExportDTO dto) {
        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()&&materialRepository.findByMaterialCode(dto.getAssyMaterialCode()).isPresent()) {

            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            ProductionPlan productionPlan = productionPlanRepository.findByProductionPlanCode(dto.getProductionPlanCode());
            Material assyMaterial = materialRepository.findByMaterialCode(dto.getAssyMaterialCode()).get();

            return Export.builder()
                    .exportId(dto.getExportId())
                    .material(material)
                    .assyMaterial(assyMaterial)
                    .assyQuantity(dto.getAssyQuantity())
                    .productionPlan(productionPlan)
                    .requiredQuantity(dto.getRequiredQuantity())
                    .exportStatus(CurrentStatus.ON_HOLD)
                    .build();

        } else {
            return null;
        }
    }

    public ExportDTO entityToDto(Export entity) {

        String exportStatus;
        switch (entity.getExportStatus()) {
            case ON_HOLD:
                exportStatus = "대기 중";
                break;
            case APPROVAL:
                exportStatus = "승인 완료";
                break;
            case FINISHED:
                exportStatus = "종료";
                break;
            default:
                exportStatus = null;
        }

        ExportDTO dto = ExportDTO.builder()
                .exportId(entity.getExportId())
                .exportCode(entity.getExportCode())
                .productionPlanCode(entity.getProductionPlan().getProductionPlanCode())
                .productionStartDate(entity.getProductionPlan().getProductionStartDate())
                .productionEndDate(entity.getProductionPlan().getProductionEndDate())
                .productName(entity.getProductionPlan().getProductName())
                .materialName(entity.getMaterial().getMaterialName())
                .materialCode(entity.getMaterial().getMaterialCode())
                .assyMaterialName(entity.getAssyMaterial().getMaterialName())
                .assyMaterialCode(entity.getAssyMaterial().getMaterialCode())
                .assyQuantity(entity.getAssyQuantity())
                .requiredQuantity(entity.getRequiredQuantity())
                .exportStatus(exportStatus)
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .build();
        return dto;

    }


    private String generateProductionPlanCode(ExportDTO dto) {
        // 매터리얼 코드에 따른 접두어 설정
        String prefix = "EXP";
        switch (dto.getMaterialCode().substring(3, 5)) {
            case "WH":
                prefix += "WH";
                break;
            case "RI":
                prefix += "RI";
                break;
            case "HA":
                prefix += "HA";
                break;
            case "SA":
                prefix += "SA";
                break;
            case "PE":
                prefix += "PE";
                break;
            case "BO":
                prefix += "BO";
                break;
            case "B1":
                prefix += "B1";
                break;
            case "B2":
                prefix += "B2";
                break;
            case "B3":
                prefix += "B3";
                break;
            case "K1":
                prefix += "K1";
                break;
            case "K2":
                prefix += "K2";
                break;
            default:
                prefix += "UN";
                break;
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        LocalDateTime today = LocalDateTime.now();
        String dateCode = today.format(formatter);

        // 동일 접두어 코드의 다음 번호
        // 한번에 등록하니까 이 코드가 같아서 문제가 생김
        Long nextSequence = exportRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }


    @Override
    public List<ExportDTO> getCurrentExportList() {
        List<Object[]> result = exportRepository.findByExportStatusOnHold();

        List<Export> exports = new ArrayList<>();
        List<Stock> stocks = new ArrayList<>();
        result.forEach(arr -> {
            Export export = (Export) arr[0];
            Stock stock = (Stock) arr[1];
            exports.add(export);
            stocks.add(stock);
        });
        List<ExportDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            ExportDTO dto = entityToDto(exports.get(i));
            dto.setAvailableQuantity(stocks.get(i).getQuantity());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public Page<ExportDTO> getCurrentExportListPage(Pageable pageable) {
        Page<Object[]> result = exportRepository.findByExportStatusOnHold(pageable);

        Function<Object[], ExportDTO> fn = (arr->entitiesToDto(
                (Export) arr[0],
                (Stock) arr[1]
        ));

        return result.map(fn);
    }

    public ExportDTO entitiesToDto(Export entity, Stock stock) {

        String exportStatus;
        switch (entity.getExportStatus()) {
            case ON_HOLD:
                exportStatus = "대기 중";
                break;
            case APPROVAL:
                exportStatus = "승인 완료";
                break;
            case FINISHED:
                exportStatus = "종료";
                break;
            default:
                exportStatus = null;
        }

        ExportDTO dto = ExportDTO.builder()
                .exportId(entity.getExportId())
                .exportCode(entity.getExportCode())
                .productionPlanCode(entity.getProductionPlan().getProductionPlanCode())
                .productionStartDate(entity.getProductionPlan().getProductionStartDate())
                .productionEndDate(entity.getProductionPlan().getProductionEndDate())
                .productName(entity.getProductionPlan().getProductName())
                .materialName(entity.getMaterial().getMaterialName())
                .materialCode(entity.getMaterial().getMaterialCode())
                .assyMaterialName(entity.getAssyMaterial().getMaterialName())
                .assyMaterialCode(entity.getAssyMaterial().getMaterialCode())
                .assyQuantity(entity.getAssyQuantity())
                .requiredQuantity(entity.getRequiredQuantity())
                .availableQuantity(stock.getQuantity())
                .exportStatus(exportStatus)
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .build();
        return dto;

    }


    //페이징 및 검색용
    @Override
    public Page<ExportDTO> getExports(Pageable pageable) {
        Page<Export> result = exportRepository.findAll(pageable);
        return result.map(this::entityToDto);
    }

    //완료 건은 3일 이내의 것만 출력하고 싶음.....
    @Override
    public Page<ExportDTO> getExportsNotHOLD(Pageable pageable){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.minusDays(3); //시작일을 3일 전으로 정한다.
        Page<Export> result = exportRepository.findByStatusNotFinished(pageable, start, today);
        return result.map(this::entityToDto);
    }


    @Override
    public Page<ExportDTO> getExportsWithSearchInExportCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByExportCode(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getExportsWithSearchInProductionPlanCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByProductionPlanCode(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getExportsWithSearchInMaterialName(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByMaterialName(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getExportsWithSearchInMaterialCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByMaterialCodeAndStatusOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getExportsWithSearchInProductName(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByProductName(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getCurrentExportsWithSearchInExportCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByExportCodeOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getCurrentExportsWithSearchInProductionPlanCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByProductionPlanCodeOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getCurrentExportsWithSearchInMaterialName(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByMaterialNameOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getCurrentExportsWithSearchInMaterialCode(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByMaterialCodeOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public Page<ExportDTO> getCurrentExportsWithSearchInProductName(String keyword, Pageable pageable){
        Page<Export> result = exportRepository.findByProductNameOnHold(keyword, pageable);
        return result.map(this::entityToDto);
    }

    @Override
    public int findSumByProductionPlanCodeAndMaterialCode(String productionPlanCode, String materialCode)
    {
        return exportRepository.findSumByProductionPlanCodeAndMaterialCode(productionPlanCode, materialCode);
    }

    //대시보드 출력용 출고 요청 건수
    @Override
    public int getCountCurrentRequest(){
        return exportRepository.countCurrentRequest();
    }

    //대시 보드 출력용 승인된 출고 요청 건수
    @Override
    public int getCountApprovedRequestThisMonth(){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return exportRepository.countApprovedRequest(start, end);
    }

    //대시 보드 출력용 불출 완료된 출고 요청 건수
    @Override
    public int getCountFinishedRequestThisMonth(){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime end = today.with(lastDayOfMonth()).with(LocalTime.MAX);
        return exportRepository.countFinishedRequest(start, end);
    }

    //대시보드 출력용 최근 출고요청 리스트
    @Override
    public List<ExportDTO> getRecentExportList(){
        return exportRepository.findRecentExport().stream().map(this::entityToDto).toList();
    }

}
