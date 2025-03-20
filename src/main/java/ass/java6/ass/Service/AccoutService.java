package ass.java6.ass.Service;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface AccoutService {
    Account Dangky( DangkyRequest newaccout );
    Account Dangnhap(String username ,String password);
    Account Xacthuc(HttpSession session ,int otpinput);
}
