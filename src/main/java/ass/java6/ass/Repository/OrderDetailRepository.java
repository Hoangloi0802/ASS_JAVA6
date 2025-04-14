package ass.java6.ass.Repository;

import ass.java6.ass.Entity.OrderDetail;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
        @Query("SELECT p.name, SUM(od.quantity * od.price) " +
                        "FROM Order o " +
                        "JOIN o.orderDetails od " +
                        "JOIN od.product p " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY p.name")
        List<Object[]> getRevenueByProduct(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(od.quantity) FROM Order o JOIN o.orderDetails od")
        Integer countTotalProductsSold();

        @Query("SELECT SUM(od.quantity) FROM Order o JOIN o.orderDetails od " +
                        "WHERE o.createDate BETWEEN :startDate AND :endDate")
        Integer countTotalProductsSoldByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(od.quantity) FROM Order o JOIN o.orderDetails od JOIN od.product p JOIN p.category c " +
                        "WHERE c.name = :category AND o.createDate BETWEEN :startDate AND :endDate")
        Integer countTotalProductsSoldByCategory(
                        @Param("category") String category,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product p JOIN FETCH p.category WHERE od.order.id = ?1")
        List<OrderDetail> findByOrderId(Long orderId);

        @Modifying
        @Transactional
        @Query("DELETE FROM OrderDetail od WHERE od.order.id = :orderId")
        void deleteByOrderId(@Param("orderId") Long orderId);
        
}
