package ass.java6.ass.Repository;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ass.java6.ass.Entity.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Voucher findByCode(String code);

    List<Voucher> findByCategory_Id(String categoryId);

    List<Voucher> findByExpiryDateGreaterThanEqualAndTrangThaiTrue(Date expiryDate);

    List<Voucher> findByMinOrderValueLessThanEqual(Double orderAmount);

    List<Voucher> findByTrangThaiTrue();

    List<Voucher> findByCategory_IdAndExpiryDateGreaterThanEqualAndTrangThaiTrue(
            String categoryId, Date expiryDate);

    List<Voucher> findByExpiryDateLessThanAndTrangThaiTrue(Date expiryDate);
}