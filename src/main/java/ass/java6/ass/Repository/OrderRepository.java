package ass.java6.ass.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ass.java6.ass.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
        @Query("SELECT od.product.name, SUM(od.quantity) as totalQuantity, SUM(od.price * od.quantity) as totalPrice, MAX(o.createDate) as lastOrderDate "
                        +
                        "FROM OrderDetail od JOIN od.order o " +
                        "WHERE o.account.username = :username " +
                        "GROUP BY od.product.name " +
                        "ORDER BY totalQuantity DESC")
        List<Object[]> getTopOrderedProducts(String username);

        @Query("""
                            SELECT
                                o.createDate AS time,
                                SUM(od.price * od.quantity) AS revenue,
                                COUNT(DISTINCT o.id) AS totalOrders,
                                SUM(od.quantity) AS totalProducts,
                                COUNT(DISTINCT o.account.username) AS newCustomers
                            FROM Order o
                            JOIN o.orderDetails od
                            JOIN od.product p
                            WHERE o.createDate BETWEEN :startDate AND :endDate
                            AND (:category IS NULL OR p.category.name = :category)
                            GROUP BY o.createDate
                            ORDER BY o.createDate ASC
                        """)
        List<Object[]> getRevenueStatistics(
                        @Param("category") String category,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Đếm tổng đơn hàng
        @Query("SELECT COUNT(o) FROM Order o")
        long countTotalOrders();

        // Tổng doanh thu từ tất cả đơn hàng
        @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od")
        Double calculateTotalRevenue();

        @Query("SELECT c.name, SUM(od.quantity * od.price) " +
                        "FROM OrderDetail od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "JOIN od.order o " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY c.name")
        List<Object[]> getRevenueByCategory(LocalDateTime startDate, LocalDateTime endDate);

        @Query("SELECT c.name, SUM(od.quantity * od.price) " +
                        "FROM OrderDetail od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "GROUP BY c.name")
        List<Object[]> getRevenueByCategoryNoTimeFilter();
}
