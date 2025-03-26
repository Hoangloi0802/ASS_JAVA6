package ass.java6.ass.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ass.java6.ass.Entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT p.name, SUM(od.quantity * od.price) " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.createDate BETWEEN :startDate AND :endDate " + // Sửa từ orderDate thành createDate
            "GROUP BY p.name")
    List<Object[]> getRevenueByProduct(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od")
    Integer countTotalProductsSold();
}
