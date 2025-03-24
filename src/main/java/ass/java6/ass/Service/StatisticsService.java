package ass.java6.ass.Service;

import ass.java6.ass.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> getRevenueStatistics(String category, String startDate, String endDate, String timeUnit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDateTime startDateTime = (startDate != null && !startDate.isEmpty()) 
            ? LocalDate.parse(startDate, formatter).atStartOfDay() 
            : LocalDateTime.now().minusMonths(1);
        
        LocalDateTime endDateTime = (endDate != null && !endDate.isEmpty()) 
            ? LocalDate.parse(endDate, formatter).atTime(23, 59, 59) 
            : LocalDateTime.now();
    
        String timeFormat = switch (timeUnit) {
            case "month" -> "%Y-%m";
            case "year" -> "%Y";
            default -> "%Y-%m-%d";
        };

        List<Object[]> statistics = orderRepository.getRevenueStatistics(category, startDateTime, endDateTime, timeFormat);

        Map<String, Object> result = new HashMap<>();
        result.put("labels", statistics.stream().map(row -> String.valueOf(row[0])).toList());
        result.put("revenue", statistics.stream().map(row -> ((Number) row[1]).doubleValue()).toList());
        result.put("totalOrders", statistics.stream().map(row -> ((Number) row[2]).intValue()).toList());
        result.put("totalProducts", statistics.stream().map(row -> ((Number) row[3]).intValue()).toList());
        result.put("newCustomers", statistics.stream().map(row -> ((Number) row[4]).intValue()).toList());

        return result;
    }
}
