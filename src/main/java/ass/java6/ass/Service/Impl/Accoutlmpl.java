package ass.java6.ass.Service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import ass.java6.ass.Config.SendEmailConfig;
import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Dto.DangnhapRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.AccoutService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class Accoutlmpl implements AccoutService {

    @Autowired
    private SendEmailConfig sendEmailConfig;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }


    @Override
    public Account Dangky(@Valid @ModelAttribute("accoutDangky") DangkyRequest newaccout) {
        if (accountRepository.findByUsername(newaccout.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại!");
        }
        if (accountRepository.findByEmail(newaccout.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (newaccout.getUsername().equals(newaccout.getPassword())) {
            throw new IllegalArgumentException("Tài khoản không được trùng với mật khẩu ");
        }
        Account a = new Account();
        a.setUsername(newaccout.getUsername());
        a.setFullname(newaccout.getFullname());
        a.setPassword(bCryptPasswordEncoder.encode(newaccout.getPassword()));
        a.setActivated(false);
        a.setEmail(newaccout.getEmail());
        a.setMobile(newaccout.getMobile());
        a.setRole(Role.ROLE_USER);

        return accountRepository.save(a);

    }

    @Override
    public Account Xacthuc(HttpSession session, Integer otpinput) {
    
    Object otpObj = session.getAttribute("otp");
    if (otpObj == null) {
        throw new IllegalArgumentException("OTP đã hết hạn hoặc không tồn tại.");
    }
    int otpFromSession;
    try {
        otpFromSession = Integer.parseInt(otpObj.toString());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("OTP không hợp lệ.");
    }
    Account account = (Account) session.getAttribute("account");
    if (account == null) {
        throw new IllegalArgumentException("Không tìm thấy tài khoản trong session.");
    }
    if (otpinput != otpFromSession) {
        throw new IllegalArgumentException("❌ Mã OTP không đúng. Vui lòng thử lại.");
        
    } else {
        account.setActivated(true);
        accountRepository.save(account);
        session.removeAttribute("otp"); 
        return account;
    }
}


    @Override
    public void Checkotpquenmk(HttpSession session, String otpInput) {
        Object otpObj = session.getAttribute("otp");

        if (otpObj == null) {
            throw new IllegalArgumentException("OTP đã hết hạn hoặc không tồn tại.");
        }
    
        String otpFromSession = otpObj.toString().trim();
        if (!otpFromSession.equals(otpInput.trim())) {
            throw new IllegalArgumentException("OTP không đúng.");
        }
        session.removeAttribute("otp");   
    }


    @Override
    public void doiMatKhau(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại."));
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        account.setPassword(encodedPassword);
        Account updatedAccount = accountRepository.save(account);
        System.out.println("Email: " + email + ", Mật khẩu mới: " + updatedAccount.getPassword());
    }
}
