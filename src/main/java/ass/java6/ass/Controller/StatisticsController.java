package ass.java6.ass.Controller;

import ass.java6.ass.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/revenue")
    public Map<String, Object> getRevenueStatistics(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "day") String timeUnit) {
        return statisticsService.getRevenueStatistics(category, startDate, endDate, timeUnit);
    }
}
