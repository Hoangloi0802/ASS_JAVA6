package ass.java6.ass.Service;

import ass.java6.ass.Entity.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherService {
    List<Voucher> findAll();

    Optional<Voucher> findById(Integer id);

    Voucher save(Voucher voucher);

    void deleteById(Integer id);

    Voucher findByCode(String code);

    List<Voucher> findByCategory(String categoryId);

    List<Voucher> findActiveVouchers();

    List<Voucher> findByTrangThaiTrue();

    List<Voucher> getValidVouchers(double totalOrderValue);

    void updateExpiredVouchers();

    List<Voucher> findValidVouchersForCategory(String categoryId, Double orderAmount);
}