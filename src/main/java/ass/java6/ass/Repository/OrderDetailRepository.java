package ass.java6.ass.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.OrderDetail;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
