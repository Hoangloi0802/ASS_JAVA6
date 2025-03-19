package ass.java6.ass.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
