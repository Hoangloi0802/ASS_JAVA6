package ass.java6.ass.Service;

import java.util.Optional;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Dto.DangnhapRequest;
import ass.java6.ass.Entity.Account;
import jakarta.servlet.http.HttpSession;

public interface AccoutService {
    Account save(Account account);
    Account Dangky( DangkyRequest newaccout );
    Account Xacthuc(HttpSession session ,Integer otpinput);
    Optional<Account> findByUsername(String username);
     void Checkotpquenmk(HttpSession session ,Integer otp);
     void doiMatKhau(String email, String password);
}
