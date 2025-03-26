package ass.java6.ass.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ass.java6.ass.Entity.Voucher;
import ass.java6.ass.Repository.VoucherRepository;
import ass.java6.ass.Service.VoucherService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    private VoucherRepository voucherRepository; // ✅ Sửa lại tên

    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    @Override
    public Optional<Voucher> findById(Integer id) {
        return voucherRepository.findById(id);
    }

    @Override
    public Voucher save(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteById(Integer id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public Voucher findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    @Override
    public List<Voucher> findByCategory(String categoryId) {
        return voucherRepository.findByCategory_Id(categoryId);
    }

    @Override
    public List<Voucher> findActiveVouchers() {
        return voucherRepository.findByExpiryDateGreaterThanEqualAndTrangThaiTrue(
                new Date(System.currentTimeMillis()));
    }

    @Override
    public List<Voucher> findValidVouchersForCategory(String categoryId, Double orderAmount) {
        return voucherRepository.findByCategory_IdAndExpiryDateGreaterThanEqualAndTrangThaiTrue(
                categoryId, new Date(System.currentTimeMillis()))
                .stream()
                .filter(v -> v.getMinOrderValue() <= orderAmount)
                .toList();
    }
}
