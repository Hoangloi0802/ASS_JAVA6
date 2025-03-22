package ass.java6.ass.Service;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;

public interface AccoutService {
    Account Dangky( DangkyRequest newaccout);
    Account Dangnhap(String username ,String password);
}
