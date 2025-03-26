package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class NhanSuController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/admin/nhansu")
    public String nhansu(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "admin/Quanlynhansu";
    }

    @GetMapping("/nhansu/detail/{username}")
    @ResponseBody
    public ResponseEntity<?> getNhanSuDetail(@PathVariable("username") String username) {
        System.out.println("Nhận request với username: " + username);

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Username không hợp lệ!"));
        }

        Optional<Account> accountOpt = accountRepository.findById(username.trim());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            return ResponseEntity.ok(Map.of(
                    "username", account.getUsername(),
                    "fullname", account.getFullname(),
                    "email", account.getEmail(),
                    "role", account.getRole().name()));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy nhân viên!"));
        }
    }

    @PostMapping("/admin/nhansu/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateNhanSu(
            @RequestParam("username") String username,
            @RequestParam("role") String role) {

        Map<String, String> response = new HashMap<>();

        // Input validation
        if (username == null || username.trim().isEmpty()) {
            response.put("error", "Username không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        if (role == null || role.trim().isEmpty()) {
            response.put("error", "Vai trò không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Find account
            Optional<Account> accountOpt = accountRepository.findById(username.trim());
            if (accountOpt.isEmpty()) {
                response.put("error", "Không tìm thấy nhân viên: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validate and set role
            Account account = accountOpt.get();
            String normalizedRole = role.trim().toUpperCase();

            // Validate role exists
            if (!Arrays.asList("ROLE_USER", "ROLE_ADMIN").contains(normalizedRole)) {
                response.put("error", "Vai trò không hợp lệ!");
                return ResponseEntity.badRequest().body(response);
            }

            account.setRole(Role.valueOf(normalizedRole));
            accountRepository.save(account);

            response.put("message", "Cập nhật vai trò thành công!");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", "Vai trò không hợp lệ: " + role);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Lỗi hệ thống khi cập nhật vai trò: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
