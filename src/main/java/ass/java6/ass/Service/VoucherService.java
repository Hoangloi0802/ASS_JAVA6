package ass.java6.ass.Service;

import java.util.List;
import java.util.Optional;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.Voucher;

public interface VoucherService {
    List<Voucher> findAll();

    Optional<Voucher> findById(Integer id);

    Voucher save(Voucher voucher);

    void deleteById(Integer id);

    Voucher findByCode(String code);

    List<Voucher> findByCategory(String categoryId);
    List<Voucher> getValidVouchers(double totalOrderValue);
    List<Voucher> findActiveVouchers();
    List<Voucher> findByTrangThaiTrue();
    List<Voucher> findValidVouchersForCategory(String categoryId, Double orderAmount);
   
}
