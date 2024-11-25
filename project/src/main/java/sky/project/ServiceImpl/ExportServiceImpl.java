package sky.project.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sky.project.DTO.ExportDTO;
import sky.project.Entity.*;
import sky.project.Repository.ExportRepository;
import sky.project.Repository.MaterialRepository;
import sky.project.Repository.ProductionPlanRepository;
import sky.project.Service.ExportService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final MaterialRepository materialRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ExportRepository exportRepository;

    @Override
    public Long register(ExportDTO dto) {
        Export entity = dtoToEntity(dto);
        entity.setExportCode(generateProductionPlanCode(dto));
        exportRepository.save(entity);
        return entity.getExportId();
    }

    public Export dtoToEntity(ExportDTO dto) {
        if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {

            Material material = materialRepository.findByMaterialCode(dto.getMaterialCode()).get();
            ProductionPlan productionPlan = productionPlanRepository.findByProductionPlanCode(dto.getProductionPlanCode());

            return Export.builder()
                    .exportId(dto.getExportId())
                    .material(material)
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
                .productName(entity.getProductionPlan().getProductName())
                .materialName(entity.getMaterial().getMaterialName())
                .materialCode(entity.getMaterial().getMaterialCode())
                .requiredQuantity(entity.getRequiredQuantity())
                .exportStatus(exportStatus)
                .createdDate(entity.getCreatedDate())
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
}
