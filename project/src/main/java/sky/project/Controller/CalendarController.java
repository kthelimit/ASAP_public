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
        List<ProductionPlan> plans = productionPlanRepository.findAll();
        List<Map<String, Object>> events = new ArrayList<>();

        for(ProductionPlan plan : plans){
            Map<String, Object> event = new HashMap<>();
            event.put("title",plan.getProductName());
            event.put("start",plan.getProductionStartDate().toString());
            event.put("end",plan.getProductionEndDate().toString());
            event.put("quantity", plan.getProductionQuantity());
            events.add(event);
        }
return events;
    }

}
