package ass.java6.ass.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ass.java6.ass.Entity.Account;
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
                            c.name AS category,
                            SUM(od.price * od.quantity) AS revenue,
                            COUNT(DISTINCT o.id) AS totalOrders,
                            SUM(od.quantity) AS totalProducts
                        FROM Order o
                        JOIN o.orderDetails od
                        JOIN od.product p
                        JOIN p.category c
                        WHERE o.createDate BETWEEN :startDate AND :endDate
                        GROUP BY c.name
                        ORDER BY revenue DESC
                        """)
        List<Object[]> getRevenueStatisticsByCategory(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(o) FROM Order o")
        long countTotalOrders();

        @Query("SELECT COUNT(o) FROM Order o WHERE o.createDate BETWEEN :startDate AND :endDate")
        long countTotalOrdersByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(o) FROM Order o JOIN o.orderDetails od JOIN od.product p JOIN p.category c " +
                        "WHERE c.name = :category AND o.createDate BETWEEN :startDate AND :endDate")
        long countTotalOrdersByCategory(
                        @Param("category") String category,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(od.price * od.quantity) FROM Order o JOIN o.orderDetails od")
        Double calculateTotalRevenue();

        @Query("SELECT SUM(od.price * od.quantity) FROM Order o JOIN o.orderDetails od " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate")
        Double calculateTotalRevenueByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(od.price * od.quantity) FROM Order o JOIN o.orderDetails od JOIN od.product p JOIN p.category c "
                        +
                        "WHERE c.name = :category AND o.createDate BETWEEN :startDate AND :endDate")
        Double calculateTotalRevenueByCategory(
                        @Param("category") String category,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT c.name, SUM(od.quantity * od.price) " +
                        "FROM Order o " +
                        "JOIN o.orderDetails od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY c.name " +
                        "ORDER BY SUM(od.quantity * od.price) DESC")
        List<Object[]> getRevenueByCategory(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT c.name, SUM(od.quantity * od.price) " +
                        "FROM Order o " +
                        "JOIN o.orderDetails od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "GROUP BY c.name " +
                        "ORDER BY SUM(od.quantity * od.price) DESC")
        List<Object[]> getRevenueByCategoryNoTimeFilter();

        @Query("SELECT c.name, SUM(od.quantity) " +
                        "FROM Order o " +
                        "JOIN o.orderDetails od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY c.name " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<Object[]> getProductQuantityByCategory(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT c.name, SUM(od.quantity) " +
                        "FROM Order o " +
                        "JOIN o.orderDetails od " +
                        "JOIN od.product p " +
                        "JOIN p.category c " +
                        "GROUP BY c.name " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<Object[]> getProductQuantityByCategoryNoTimeFilter();
        Page<Order> findByAccountUsernameAndStatusNotIn(String username, List<String> statuses, Pageable pageable);


        Optional<Order> findByAccountAndStatus(Account account, String status);
        Page<Order> findByAccountUsername(String username, Pageable pageable);
        List<Order> findByAccount_Username(String username);

        @Query("SELECT o FROM Order o WHERE o.id = :id")
        Page<Order> findByIdContaining(Long id, Pageable pageable);
}
