package ass.java6.ass.Service;

import org.springframework.web.multipart.MultipartFile;
import ass.java6.ass.Entity.Account;

public interface ProfileService {
    Account getCurrentUser(String username);
    void updateProfile(Account updatedUser, MultipartFile file);
}
