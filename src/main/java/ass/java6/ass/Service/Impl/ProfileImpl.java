package ass.java6.ass.Service.Impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.ProfileService;

import java.util.Optional;

@Service
public class ProfileImpl implements ProfileService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getCurrentUser(String username) {
        Optional<Account> opt = accountRepository.findByUsername(username);
        return opt.orElse(null);
    }

    @Override
    public void updateProfile(Account updatedUser, MultipartFile file) {
        Optional<Account> opt = accountRepository.findByUsername(updatedUser.getUsername());
        if (opt.isPresent()) {
            Account account = opt.get();
            account.setFullname(updatedUser.getFullname());
            account.setEmail(updatedUser.getEmail());
            account.setMobile(updatedUser.getMobile());

            // Xử lý avatar upload
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = file.getOriginalFilename();
                    String uploadDir = "src/main/resources/static/img/profile/";
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    try (InputStream inputStream = file.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        account.setPhoto("/img/profile/" + fileName);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // Xử lý lỗi (nếu muốn)
                }
            }
            accountRepository.save(account);
        }
    }
}
