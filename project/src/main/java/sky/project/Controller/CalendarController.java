package sky.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.project.Entity.ProductionPlan;
import sky.project.Repository.ProductionPlanRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    ProductionPlanRepository productionPlanRepository;

    @GetMapping("/events")
    public List<Map<String, Object>> getCalendarEvents() {
        // 데이터베이스에서 모든 생산 계획 조회
        List<ProductionPlan> plans = productionPlanRepository.findAll();
        List<Map<String, Object>> events = new ArrayList<>();

        for (ProductionPlan plan : plans) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", plan.getProductName()); // 제품명
            event.put("start", plan.getProductionStartDate().toString()); // 시작일
            event.put("end", plan.getProductionEndDate().toString()); // 종료일
            event.put("quantity", plan.getProductionQuantity()); // 생산 수량

            // 제품별 색상 지정
            String color;
            switch (plan.getProductName()) {
                case "전기자전거A":
                    color = " #0056b3";
                    break;
                case "전기자전거B":
                    color = "deepskyblue";
                    break;
                case "전동킥보드":
                    color = "#9b51e0";
                    break;
                default:
                    color = "gray"; // 기본 색상
                    break;
            }
            event.put("color", color); // 색상 추가

            events.add(event);
        }

        return events;
    }
}
