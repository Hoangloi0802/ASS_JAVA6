package ass.java6.ass.Service;

import org.springframework.web.bind.annotation.RequestBody;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;
import jakarta.validation.Valid;

public interface AccoutService {
    Account Dangky( DangkyRequest newaccout);
    Account Dangnhap(String username ,String password);
}
