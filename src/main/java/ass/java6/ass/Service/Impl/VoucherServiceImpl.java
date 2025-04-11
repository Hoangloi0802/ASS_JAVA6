package ass.java6.ass.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ass.java6.ass.Entity.Voucher;
import ass.java6.ass.Repository.VoucherRepository;
import ass.java6.ass.Service.VoucherService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

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

    @Override
    public List<Voucher> findByTrangThaiTrue() {
        Date currentDate = new Date(System.currentTimeMillis());
        return voucherRepository.findByTrangThaiTrue()
                .stream()
                .filter(voucher -> !voucher.getExpiryDate().before(currentDate))
                .toList();
    }

    @Override
    public List<Voucher> getValidVouchers(double totalOrderValue) {
        Date currentDate = new Date(System.currentTimeMillis());
        List<Voucher> allVouchers = voucherRepository.findByTrangThaiTrue();
        return allVouchers.stream()
                .filter(voucher -> !voucher.getExpiryDate().before(currentDate))
                .filter(voucher -> totalOrderValue >= voucher.getMinOrderValue())
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Chạy tự động mỗi ngày lúc 0h
    public void updateExpiredVouchers() {
        try {
            Date currentDate = new Date(System.currentTimeMillis());
            List<Voucher> expiredVouchers = voucherRepository.findByExpiryDateLessThanAndTrangThaiTrue(currentDate);

            if (!expiredVouchers.isEmpty()) {
                expiredVouchers.forEach(voucher -> {
                    voucher.setTrangThai(false);
                    System.out.println("Voucher " + voucher.getCode() + " đã hết hạn và được cập nhật trạng thái.");
                });
                voucherRepository.saveAll(expiredVouchers);
                System.out.println("Đã cập nhật trạng thái cho " + expiredVouchers.size()
                        + " voucher hết hạn vào " + currentDate);
            } else {
                System.out.println("Không có voucher nào hết hạn cần cập nhật vào " + currentDate);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái voucher hết hạn: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}