package ass.java6.ass.Service;

import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Repository.OrderDetailRepository;
import ass.java6.ass.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

        public Map<String, Object> getRevenueByProduct(String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                List<Object[]> productRevenue;
                try {
                        productRevenue = orderDetailRepository.getRevenueByProduct(start, end);
                } catch (Exception e) {
                        System.out.println("Error getting product revenue: " + e.getMessage());
                        productRevenue = Collections.emptyList();
                }

                Map<String, Object> result = new HashMap<>();

                if (productRevenue == null || productRevenue.isEmpty()) {
                        result.put("products", Collections.emptyList());
                        result.put("revenue", Collections.emptyList());
                } else {
                        // Đảm bảo không có giá trị null trong dữ liệu
                        List<String> products = productRevenue.stream()
                                        .filter(row -> row[0] != null)
                                        .map(row -> row[0].toString())
                                        .collect(Collectors.toList());

                        List<Double> revenue = productRevenue.stream()
                                        .filter(row -> row[0] != null && row[1] != null)
                                        .map(row -> ((Number) row[1]).doubleValue())
                                        .collect(Collectors.toList());

                        result.put("products", products);
                        result.put("revenue", revenue);
                }

                return result;
        }

        public Map<String, Object> getRevenueByCategory(String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                List<Object[]> categoryRevenue;

                try {
                        if (startDate != null && endDate != null) {
                                categoryRevenue = orderRepository.getRevenueByCategory(start, end);
                        } else {
                                categoryRevenue = orderRepository.getRevenueByCategoryNoTimeFilter();
                        }
                } catch (Exception e) {
                        System.out.println("Error getting category revenue: " + e.getMessage());
                        categoryRevenue = Collections.emptyList();
                }

                System.out.println("Raw category revenue data: " + categoryRevenue);

                Map<String, Object> result = new HashMap<>();
                if (categoryRevenue == null || categoryRevenue.isEmpty()) {
                        System.out.println("⚠ Không có dữ liệu doanh thu theo loại sản phẩm!");
                        result.put("categories", Collections.emptyList());
                        result.put("revenue", Collections.emptyList());
                } else {
                        // Đảm bảo không có giá trị null trong dữ liệu
                        List<String> categories = categoryRevenue.stream()
                                        .filter(row -> row[0] != null)
                                        .map(row -> row[0].toString())
                                        .collect(Collectors.toList());

                        List<Double> revenue = categoryRevenue.stream()
                                        .filter(row -> row[0] != null && row[1] != null)
                                        .map(row -> ((Number) row[1]).doubleValue())
                                        .collect(Collectors.toList());

                        System.out.println("✅ Dữ liệu doanh thu: " + categories + " - " + revenue);
                        result.put("categories", categories);
                        result.put("revenue", revenue);
                }
                return result;
        }

        public Map<String, Object> getProductQuantityByCategory(String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                List<Object[]> categoryQuantity;

                try {
                        if (startDate != null && endDate != null) {
                                categoryQuantity = orderRepository.getProductQuantityByCategory(start, end);
                        } else {
                                categoryQuantity = orderRepository.getProductQuantityByCategoryNoTimeFilter();
                        }
                } catch (Exception e) {
                        System.out.println("Error getting product quantity by category: " + e.getMessage());
                        categoryQuantity = Collections.emptyList();
                }

                System.out.println("Raw category quantity data: " + categoryQuantity);

                Map<String, Object> result = new HashMap<>();
                if (categoryQuantity == null || categoryQuantity.isEmpty()) {
                        System.out.println("⚠ Không có dữ liệu số lượng sản phẩm theo loại sản phẩm!");
                        result.put("categories", Collections.emptyList());
                        result.put("quantities", Collections.emptyList());
                } else {
                        // Đảm bảo không có giá trị null trong dữ liệu
                        List<String> categories = categoryQuantity.stream()
                                        .filter(row -> row[0] != null)
                                        .map(row -> row[0].toString())
                                        .collect(Collectors.toList());

                        List<Long> quantities = categoryQuantity.stream()
                                        .filter(row -> row[0] != null && row[1] != null)
                                        .map(row -> ((Number) row[1]).longValue())
                                        .collect(Collectors.toList());

                        System.out.println("✅ Dữ liệu số lượng: " + categories + " - " + quantities);
                        result.put("categories", categories);
                        result.put("quantities", quantities);
                }
                return result;
        }

        public Map<String, Object> getRevenueStatistics(String category, String startDate, String endDate,
                        String timeUnit) {
                Map<String, Object> statistics = new HashMap<>();

                try {
                        // Lấy dữ liệu theo loại sản phẩm và khoảng thời gian
                        Map<String, Object> categoryStats = getRevenueByCategory(startDate, endDate);
                        Map<String, Object> quantityStats = getProductQuantityByCategory(startDate, endDate);

                        // Đảm bảo dữ liệu cho cả bảng và biểu đồ là giống nhau
                        statistics.put("categories", categoryStats.get("categories"));
                        statistics.put("categoryRevenue", categoryStats.get("revenue"));
                        statistics.put("quantityCategories", quantityStats.get("categories"));
                        statistics.put("quantities", quantityStats.get("quantities"));

                        // Lấy tổng số liệu với bộ lọc
                        statistics.put("totalOrders", getTotalOrders(category, startDate, endDate));
                        statistics.put("totalRevenue", getTotalRevenue(category, startDate, endDate));
                        statistics.put("totalProducts", getTotalProductsSold(category, startDate, endDate));
                        statistics.put("newCustomers", getNewCustomers());
                } catch (Exception e) {
                        System.out.println("Error getting revenue statistics: " + e.getMessage());
                        e.printStackTrace();

                        // Provide default empty values in case of error
                        statistics.put("categories", Collections.emptyList());
                        statistics.put("categoryRevenue", Collections.emptyList());
                        statistics.put("quantityCategories", Collections.emptyList());
                        statistics.put("quantities", Collections.emptyList());
                        statistics.put("totalOrders", 0L);
                        statistics.put("totalRevenue", 0.0);
                        statistics.put("totalProducts", 0);
                        statistics.put("newCustomers", 0L);
                }

                System.out.println("Statistics data: " + statistics);
                return statistics;
        }

        // Phương thức mới để lấy thống kê chi tiết theo loại sản phẩm và khoảng thời
        // gian
        public List<Map<String, Object>> getCategoryStatistics(String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                try {
                        List<Object[]> results = orderRepository.getRevenueStatisticsByCategory(start, end);

                        return results.stream().map(row -> {
                                Map<String, Object> item = new HashMap<>();
                                item.put("category", row[0]);
                                item.put("revenue", row[1]);
                                item.put("orderCount", row[2]);
                                item.put("productCount", row[3]);
                                return item;
                        }).collect(Collectors.toList());
                } catch (Exception e) {
                        System.out.println("Error getting category statistics: " + e.getMessage());
                        e.printStackTrace();
                        return Collections.emptyList();
                }
        }

        public long getTotalOrders(String category, String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                long totalOrders = 0;
                try {
                        if (category != null && !category.isEmpty()) {
                                totalOrders = orderRepository.countTotalOrdersByCategory(category, start, end);
                        } else if (startDate != null && endDate != null) {
                                totalOrders = orderRepository.countTotalOrdersByDateRange(start, end);
                        } else {
                                totalOrders = orderRepository.countTotalOrders();
                        }
                } catch (Exception e) {
                        System.out.println("Error getting total orders: " + e.getMessage());
                }

                System.out.println("Total Orders: " + totalOrders);
                return totalOrders;
        }

        public double getTotalRevenue(String category, String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                Double revenue = 0.0;
                try {
                        if (category != null && !category.isEmpty()) {
                                revenue = orderRepository.calculateTotalRevenueByCategory(category, start, end);
                        } else if (startDate != null && endDate != null) {
                                revenue = orderRepository.calculateTotalRevenueByDateRange(start, end);
                        } else {
                                revenue = orderRepository.calculateTotalRevenue();
                        }
                } catch (Exception e) {
                        System.out.println("Error getting total revenue: " + e.getMessage());
                }

                System.out.println("Total Revenue: " + revenue);
                return revenue != null ? revenue : 0.0;
        }

        public int getTotalProductsSold(String category, String startDate, String endDate) {
                LocalDateTime start = parseStartDate(startDate);
                LocalDateTime end = parseEndDate(endDate);

                Integer productsSold = 0;
                try {
                        if (category != null && !category.isEmpty()) {
                                productsSold = orderDetailRepository.countTotalProductsSoldByCategory(category, start,
                                                end);
                        } else if (startDate != null && endDate != null) {
                                productsSold = orderDetailRepository.countTotalProductsSoldByDateRange(start, end);
                        } else {
                                productsSold = orderDetailRepository.countTotalProductsSold();
                        }
                } catch (Exception e) {
                        System.out.println("Error getting total products sold: " + e.getMessage());
                }

                System.out.println("Total Products Sold: " + productsSold);
                return productsSold != null ? productsSold : 0;
        }

        // Sửa phương thức này để không yêu cầu tham số
        public long getNewCustomers() {
                long newCustomers = 0;
                try {
                        // Không sử dụng lọc theo ngày vì Account không có trường createDate
                        newCustomers = accountRepository.countNewCustomers();
                        System.out.println("Lấy tổng số khách hàng mới không lọc theo ngày");
                } catch (Exception e) {
                        System.out.println("Error getting new customers: " + e.getMessage());
                }

                System.out.println("New Customers: " + newCustomers);
                return newCustomers;
        }

        // Phương thức hỗ trợ để chuyển đổi chuỗi ngày thành LocalDateTime
        private LocalDateTime parseStartDate(String dateStr) {
                if (dateStr == null || dateStr.isEmpty()) {
                        return LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())
                                        .with(LocalTime.MIN);
                }

                try {
                        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                        return date.atStartOfDay();
                } catch (Exception e) {
                        System.out.println("Lỗi khi phân tích ngày bắt đầu: " + e.getMessage());
                        return LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())
                                        .with(LocalTime.MIN);
                }
        }

        private LocalDateTime parseEndDate(String dateStr) {
                if (dateStr == null || dateStr.isEmpty()) {
                        return LocalDateTime.now().with(LocalTime.MAX);
                }

                try {
                        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                        return date.atTime(LocalTime.MAX);
                } catch (Exception e) {
                        System.out.println("Lỗi khi phân tích ngày kết thúc: " + e.getMessage());
                        return LocalDateTime.now().with(LocalTime.MAX);
                }
        }
}
