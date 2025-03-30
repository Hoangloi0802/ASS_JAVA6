package ass.java6.ass.Controller;

import ass.java6.ass.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
        @Autowired
        private StatisticsService statisticsService;

        @GetMapping("/admin/statistics")
        public String getStatistics(
                        @RequestParam(value = "category", required = false) String category,
                        @RequestParam(value = "startDate", required = false) String startDate,
                        @RequestParam(value = "endDate", required = false) String endDate,
                        Model model) {

                // Ghi log các tham số đầu vào
                System.out.println("=== FILTER PARAMETERS ===");
                System.out.println("Category: " + (category != null ? category : "All"));
                System.out.println("Start Date: " + (startDate != null ? startDate : "Default (1 month ago)"));
                System.out.println("End Date: " + (endDate != null ? endDate : "Default (today)"));
                System.out.println("========================");

                // Lấy thống kê tổng hợp
                Map<String, Object> stats = statisticsService.getRevenueStatistics(category, startDate, endDate, null);

                // Lấy thống kê chi tiết theo loại sản phẩm
                List<Map<String, Object>> categoryStats = statisticsService.getCategoryStatistics(startDate, endDate);
                model.addAttribute("categoryStats", categoryStats);

                // Thêm debug để kiểm tra dữ liệu
                System.out.println("=== DEBUG CONTROLLER DATA ===");
                System.out.println("Categories: " + stats.get("categories"));
                System.out.println("Category Revenue: " + stats.get("categoryRevenue"));
                System.out.println("Quantity Categories: " + stats.get("quantityCategories"));
                System.out.println("Quantities: " + stats.get("quantities"));
                System.out.println("Total Orders: " + stats.get("totalOrders"));
                System.out.println("Total Revenue: " + stats.get("totalRevenue"));
                System.out.println("Total Products: " + stats.get("totalProducts"));
                System.out.println("New Customers: " + stats.get("newCustomers"));
                System.out.println("Category Stats: " + categoryStats);
                System.out.println("==============================");

                model.addAttribute("categories",
                                stats.get("categories") != null ? stats.get("categories") : Collections.emptyList());
                model.addAttribute("categoryRevenue",
                                stats.get("categoryRevenue") != null ? stats.get("categoryRevenue")
                                                : Collections.emptyList());
                model.addAttribute("quantityCategories",
                                stats.get("quantityCategories") != null ? stats.get("quantityCategories")
                                                : Collections.emptyList());
                model.addAttribute("quantities",
                                stats.get("quantities") != null ? stats.get("quantities") : Collections.emptyList());
                model.addAttribute("totalOrders", stats.get("totalOrders") != null ? stats.get("totalOrders") : 0);
                model.addAttribute("totalRevenue", stats.get("totalRevenue") != null ? stats.get("totalRevenue") : 0.0);
                model.addAttribute("totalProducts",
                                stats.get("totalProducts") != null ? stats.get("totalProducts") : 0);
                model.addAttribute("newCustomers", stats.get("newCustomers") != null ? stats.get("newCustomers") : 0);

                // Lưu lại các tham số lọc để hiển thị trên giao diện
                model.addAttribute("selectedCategory", category);
                model.addAttribute("selectedStartDate", startDate);
                model.addAttribute("selectedEndDate", endDate);

                if (stats.get("categories") == null || ((List<?>) stats.get("categories")).isEmpty()) {
                        System.out.println("Không có dữ liệu thực, sử dụng dữ liệu mẫu để kiểm tra biểu đồ");

                        List<String> sampleCategories = Arrays.asList("Áo", "Quần", "Giày", "Phụ kiện");
                        List<Double> sampleRevenue = Arrays.asList(1500000.0, 2300000.0, 3100000.0, 980000.0);
                        List<Long> sampleQuantities = Arrays.asList(25L, 18L, 15L, 30L);

                        model.addAttribute("categories", sampleCategories);
                        model.addAttribute("categoryRevenue", sampleRevenue);
                        model.addAttribute("quantityCategories", sampleCategories);
                        model.addAttribute("quantities", sampleQuantities);
                }

                return "admin/statistics";
        }
}
