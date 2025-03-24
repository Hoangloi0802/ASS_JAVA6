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
                    DATE_FORMAT(o.createDate, :timeFormat) AS time,
                    SUM(od.price * od.quantity) AS revenue,
                    COUNT(DISTINCT o.id) AS totalOrders,
                    SUM(od.quantity) AS totalProducts,
                    COUNT(DISTINCT o.account.username) AS newCustomers
                FROM Order o
                JOIN o.orderDetails od
                JOIN od.product p
                WHERE o.createDate BETWEEN :startDate AND :endDate
                AND (:category IS NULL OR p.category.name = :category)
                GROUP BY time
                ORDER BY time ASC
            """)
    List<Object[]> getRevenueStatistics(
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("timeFormat") String timeFormat);

}
