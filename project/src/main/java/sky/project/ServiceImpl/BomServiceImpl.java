package sky.project.ServiceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.BomDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BomRepository repository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final BomRepository bomRepository;
    private final OrderRepository orderRepository;
    private final ProcurementPlanRepository procurementPlanRepository;
    private final OrderService orderService;
    private final ProductionPlanService productionPlanService;
    private final ExportRepository exportRepository;
    private final AssyRepository assyRepository;
    private final ProcurementPlanService procurementPlanService;
    private final DeliveryRequestService deliveryRequestService;
    private final DeliveryRequestRepository deliveryRequestRepository;

    //등록
    @Override
    public Long register(BomDTO dto) {

        //해당 상품에 같은 자재로 bom이 등록되어 있는지 확인하고, 없으면 새로 등록한다.
        if (bomRepository.findByProductCodeAndMaterialCode(dto.getProductCode(), dto.getMaterialCode()) != null) {

            Bom bom = bomRepository.findByProductCodeAndMaterialCode(dto.getProductCode(), dto.getMaterialCode());
            bom.setComponentType(dto.getComponentType());
            bom.setRequireQuantity(dto.getRequireQuantity());
            repository.save(bom);
            return bom.getBomId();

        } else {


            Product product = productRepository.findByProductCode(dto.getProductCode());

            if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
                Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();

                Bom bom = Bom.builder()
                        .product(product)
                        .material(material)
                        .componentType(dto.getComponentType())
                        .requireQuantity(dto.getRequireQuantity())
                        .build();
                repository.save(bom);
                return bom.getBomId();
            } else {
                return null;
            }
        }
    }

    //삭제
    @Transactional
    @Override
    public void remove(Long BomId) {
        repository.deleteById(BomId);
    }

    //상품코드로 불러오기
    @Override
    public List<BomDTO> findWithProductCode(String productCode) {
        List<Bom> bomList = repository.findByProductCode(productCode);
        List<BomDTO> bomDTOList = new ArrayList<>();
        bomList.forEach(bom -> {
            bomDTOList.add(entityToDTO(bom));
        });
        return bomDTOList;
    }

    @Override
    public List<Bom> getbomList() {
        return repository.findAll();
    }


    public BomDTO entityToDTO(Bom entity) {

        String materialCode = entity.getMaterial().getMaterialCode();

        // 재고 계산
        Stock stock = stockRepository.findByMaterialCode(materialCode);
        int stockQuantity = (stock != null) ? stock.getQuantity() : 0;

//        // 주문 수량 가져오기
//        Integer orderQuantity = orderRepository.findTotalOrderQuantityByMaterialCode(materialCode);
//        int totalOrderQuantity = (orderQuantity != null) ? orderQuantity : 0;
//
//
//        // requireQuantity 가져오기
//        Integer requireQuantity = procurementPlanRepository.findTotalRequireQuantityByMaterialCode(materialCode);
//        int totalRequireQuantity = (requireQuantity != null) ? requireQuantity : 0;
//
//
//        // 가용재고 계산
//        int availableStock = stockQuantity + totalOrderQuantity - totalRequireQuantity;
//        availableStock = Math.max(0, availableStock);


        //출고 요청용 가용재고 계산 (현재 창고 재고 - 출고 요청중인 수량)
        int availableStock = stockService.calculateAvailableStock(stock);
        log.info("창고 재고 - 출고 요청중인 수량 : " + availableStock);

        //업체에 발주 넣어둔 남은 수량의 합
        int remainedOrderQuantity = orderService.calculateRemainedQuantityForBOMDTO(stock.getMaterial());
        log.info("업체에 발주 넣어둔 남은 수량 : " + remainedOrderQuantity);

        //현재 납품 지시중인 수량
        int deliveryingQuantity =0;
        if(deliveryRequestRepository.countByMaterialCodeAndNotFinished(materialCode)>0) {
            deliveryingQuantity = deliveryRequestService.getQuantityByMaterialCode(materialCode);
        }
        //발주를 아직 안 넣은 수량을 계산
        int NotProcuredYetQuantity = 0;
        List<ProcurementPlan> plansNotProcured = procurementPlanService.findPlanNotOrdered(materialCode);
        for (int i = 0; i < plansNotProcured.size(); i++) {
            NotProcuredYetQuantity += plansNotProcured.get(i).getProcurementQuantity();
        }
        log.info("발주를 아직 안 넣은 수량(이제 넣을 것임) : " + NotProcuredYetQuantity);

        //생산 계획의 남은 소모량 계산하기
        //발주 중인 생산계획 리스트를 가져온다.(2상품 이상에 들어가는 친구도 있음)
        //materialCode로 검색해서 Assy를 찾아보고, 최종 상품들을 가져와야겠음
        List<Assy> assyList = assyRepository.findByMaterialCode(materialCode);

        int leftQuantityForProduction = 0;
        String productCode = "";
        for (int j = 0; j < assyList.size(); j++) {
            if (productCode.isEmpty() || !productCode.equals(assyList.get(j).getProduct().getProductCode())) {
                productCode = assyList.get(j).getProduct().getProductCode();

                List<ProductionPlan> planList = productionPlanService.getProductionPlanFinished(productCode);

                for (int i = 0; i < planList.size(); i++) {
                    String productionPlanCode = planList.get(i).getProductionPlanCode();
                    int requiredQuantity = planList.get(i).getProductionQuantity() * entity.getRequireQuantity();
                    int totalExportRequestQuantity;

                    //해당 생산 계획에 대해서 출고 요청해둔 수량
                    if (exportRepository.findCountByProductionPlanCodeAndMaterialCode(productionPlanCode, materialCode) == 0) {
                        totalExportRequestQuantity = 0;
                    } else {
                        totalExportRequestQuantity = exportRepository.findSumByProductionPlanCodeAndMaterialCode(productionPlanCode, materialCode);
                    }
                    requiredQuantity -= totalExportRequestQuantity;
                    log.info("이번 요구량: " + productCode + " " + productionPlanCode + " " + j + " " + i + " " + requiredQuantity);
                    leftQuantityForProduction += requiredQuantity;
                }
            }
        }

        log.info("남은 소모량: " + leftQuantityForProduction);

        //조달계획 출력용 가용재고 계산(현재 창고 재고  - 출고 요청중인 수량 + 아직 발주 안넣은 수량(넣을거라는 가정이 되어 있음)  + 업체에 발주 넣어둔 남은 수량의 합 + 납품 지시 중인 수량- 생산 계획의 남은 소모량)
        int availavbleStockProcure = availableStock + NotProcuredYetQuantity + remainedOrderQuantity + deliveryingQuantity - leftQuantityForProduction;


        // 리드타임에 따른 날짜 계산
        LocalDate today = LocalDate.now();
        LocalDate date = today.plusDays(entity.getMaterial().getLeadTime());

        return BomDTO.builder()
                .bomId(entity.getBomId())
                .componentType(entity.getComponentType())
                .materialCode(entity.getMaterial().getMaterialCode())
                .materialName(entity.getMaterial().getMaterialName())
                .productCode(entity.getProduct().getProductCode())
                .requireQuantity(entity.getRequireQuantity())
                .dayAfterLeadTime(date)
                .availableStock(availableStock)
                .remainedOrderQuantity(remainedOrderQuantity)
                .availavbleStockProcure(availavbleStockProcure)
                .deliveryingQuantity(deliveryingQuantity)
                .build();
    }

}
