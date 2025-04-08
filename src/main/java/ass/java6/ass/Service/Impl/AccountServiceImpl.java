package ass.java6.ass.Service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ass.java6.ass.Config.SendEmailConfig;
import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.AccoutService; // Sửa tên interface
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class AccountServiceImpl implements AccoutService { // Sửa tên class

    @Autowired
    private SendEmailConfig sendEmailConfig;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Optional<Account> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tài khoản không được null hoặc rỗng");
        }
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Tài khoản không được null");
        }
        return accountRepository.save(account);
    }

    @Override
    public Account Dangky(@Valid DangkyRequest newaccout) {
        // Kiểm tra null và rỗng cho các trường bắt buộc
        if (newaccout == null || newaccout.getUsername() == null || newaccout.getPassword() == null || newaccout.getEmail() == null) {
            throw new IllegalArgumentException("Thông tin đăng ký không được null");
        }

        // Kiểm tra username đã tồn tại
        if (accountRepository.findByUsername(newaccout.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại!");
        }
        // Kiểm tra email đã tồn tại
        if (accountRepository.findByEmail(newaccout.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        // Kiểm tra username không được trùng password
        if (newaccout.getUsername().equals(newaccout.getPassword())) {
            throw new IllegalArgumentException("Tài khoản không được trùng với mật khẩu");
        }

        Account a = new Account();
        a.setUsername(newaccout.getUsername());
        a.setFullname(newaccout.getFullname());
        a.setPassword(bCryptPasswordEncoder.encode(newaccout.getPassword()));
        a.setActivated(false);
        a.setEmail(newaccout.getEmail());
        a.setMobile(newaccout.getMobile());
        a.setRole(Role.ROLE_USER);

        // Gửi email xác thực (nếu cần)
        // sendEmailConfig.sendEmail(a.getEmail(), "Xác thực tài khoản", "Mã OTP của bạn là: ...");

        return accountRepository.save(a);
    }

    @Override
    public Account Xacthuc(HttpSession session, Integer otpinput) {
        if (session == null || otpinput == null) {
            throw new IllegalArgumentException("Session hoặc OTP không được null");
        }

        Object otpObj = session.getAttribute("otp");
        if (otpObj == null) {
            throw new IllegalArgumentException("OTP đã hết hạn hoặc không tồn tại");
        }

        int otpFromSession;
        try {
            otpFromSession = Integer.parseInt(otpObj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("OTP không hợp lệ");
        }

        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản trong session");
        }

        if (otpinput != otpFromSession) {
            throw new IllegalArgumentException("❌ Mã OTP không đúng. Vui lòng thử lại");
        }

        account.setActivated(true);
        accountRepository.save(account);
        session.removeAttribute("otp");
        session.removeAttribute("account"); // Xóa account khỏi session sau khi xác thực
        return account;
    }

    @Override
    public void Checkotpquenmk(HttpSession session, Integer otpInput) {
        if (session == null || otpInput == null) {
            throw new IllegalArgumentException("Session hoặc OTP không được null");
        }

        Object otpObj = session.getAttribute("otp");
        if (otpObj == null) {
            throw new IllegalArgumentException("⚠️ OTP đã hết hạn hoặc không tồn tại");
        }

        int otpFromSession;
        try {
            otpFromSession = Integer.parseInt(otpObj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("OTP không hợp lệ");
        }


        if (otpInput != otpFromSession) {
            throw new IllegalArgumentException("❌ OTP không chính xác. Vui lòng thử lại");
        }


        session.removeAttribute("otp");
        // Có thể thêm logic để cho phép đổi mật khẩu sau khi xác thực OTP
    }

    @Override
    public void doiMatKhau(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được null hoặc rỗng");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được null hoặc rỗng");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        account.setPassword(encodedPassword);
        accountRepository.save(account);

        // Gửi email thông báo (nếu cần)
        // sendEmailConfig.sendEmail(email, "Đổi mật khẩu thành công", "Mật khẩu của bạn đã được cập nhật.");
    }
}