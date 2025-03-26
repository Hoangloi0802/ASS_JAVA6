package ass.java6.ass.Service;

import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Repository.OrderDetailRepository;
import ass.java6.ass.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private OrderDetailRepository orderDetailRepository;
        @Autowired
        private AccountRepository accountRepository;

        // Thêm phương thức mới để lấy doanh thu theo từng sản phẩm
        public Map<String, Object> getRevenueByProduct(String startDate, String endDate) {
                List<Object[]> productRevenue = orderDetailRepository.getRevenueByProduct(
                                startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1),
                                endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now());

                Map<String, Object> result = new HashMap<>();
                result.put("products", productRevenue.stream().map(row -> row[0].toString()).toList());
                result.put("revenue", productRevenue.stream().map(row -> ((Number) row[1]).doubleValue()).toList());

                return result;
        }

        public Map<String, Object> getRevenueByCategory() {
                List<Object[]> categoryRevenue = orderRepository.getRevenueByCategoryNoTimeFilter();

                Map<String, Object> result = new HashMap<>();
                if (categoryRevenue == null || categoryRevenue.isEmpty()) {
                        System.out.println("⚠ Không có dữ liệu doanh thu theo loại sản phẩm!");
                        result.put("categories", Collections.emptyList());
                        result.put("revenue", Collections.emptyList());
                } else {
                        List<String> categories = categoryRevenue.stream().map(row -> row[0].toString()).toList();
                        List<Double> revenue = categoryRevenue.stream().map(row -> ((Number) row[1]).doubleValue())
                                        .toList();

                        System.out.println("✅ Dữ liệu thống kê: " + categories + " - " + revenue);
                        result.put("categories", categories);
                        result.put("revenue", revenue);
                }
                return result;
        }

        // Phương thức hiện có
        public Map<String, Object> getRevenueStatistics(String category, String startDate, String endDate,
                        String timeUnit) {
                Map<String, Object> statistics = new HashMap<>();

                // Gọi hàm thống kê doanh thu theo sản phẩm thay vì loại sản phẩm
                Map<String, Object> revenueByProduct = getRevenueByProduct(startDate, endDate);

                statistics.put("labels", revenueByProduct.get("products"));
                statistics.put("revenue", revenueByProduct.get("revenue"));
                statistics.put("totalOrders", getTotalOrders());
                statistics.put("totalProducts", getTotalProductsSold());
                statistics.put("newCustomers", getNewCustomers());

                return statistics;
        }

        public long getTotalOrders() {
                return orderRepository.countTotalOrders();
        }

        public double getTotalRevenue() {
                Double revenue = orderRepository.calculateTotalRevenue();
                return revenue != null ? revenue : 0;
        }

        public int getTotalProductsSold() {
                Integer productsSold = orderDetailRepository.countTotalProductsSold();
                return productsSold != null ? productsSold : 0;
        }

        public long getNewCustomers() {
                return accountRepository.countNewCustomers();
        }
}