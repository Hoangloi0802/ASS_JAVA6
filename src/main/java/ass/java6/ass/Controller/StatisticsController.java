package ass.java6.ass.Controller;

import ass.java6.ass.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public String showStatistics(@RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "day") String timeUnit,
            Model model) {

        System.out.println("🔹 Thống kê từ " + startDate + " đến " + endDate);

        // Lấy dữ liệu tổng quan
        Map<String, Object> statistics = statisticsService.getRevenueStatistics(category, startDate, endDate, timeUnit);

        // Đưa dữ liệu vào Model để hiển thị trên giao diện
        model.addAttribute("labels", statistics.getOrDefault("labels", ""));
        model.addAttribute("revenue", statistics.getOrDefault("revenue", ""));
        model.addAttribute("totalOrders", statistics.getOrDefault("totalOrders", 0));
        model.addAttribute("totalProducts", statistics.getOrDefault("totalProducts", 0));
        model.addAttribute("newCustomers", statistics.getOrDefault("newCustomers", 0));
        model.addAttribute("totalRevenue", statisticsService.getTotalRevenue());

        return "admin/statistics";
    }

    @GetMapping("/revenueByCategory")
    public ResponseEntity<Map<String, Object>> getRevenueByCategory() {
        Map<String, Object> data = statisticsService.getRevenueByCategory();
        return ResponseEntity.ok(data);
    }
}