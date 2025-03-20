package ass.java6.ass.Service;

import java.util.Optional;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;
import jakarta.servlet.http.HttpSession;

public interface AccoutService {
    Account Dangky( DangkyRequest newaccout );
    Account Dangnhap(String username ,String password);
    Account Xacthuc(HttpSession session ,int otpinput);
    Optional<Account> findByUsername(String username);
}
