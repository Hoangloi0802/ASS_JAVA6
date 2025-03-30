package ass.java6.ass.Controller;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/account")
public class AccountController {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public String listAccounts(@RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "role", required = false) String role,
                               @RequestParam(name = "status", required = false) String status,
                               @RequestParam(name = "username", required = false) String username,
                               Model model) {
        try {
            List<Account> accounts;
            if (keyword != null && !keyword.trim().isEmpty()) {
                accounts = accountRepository.findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCase(keyword, keyword);
            } else {
                accounts = accountRepository.findAll();
            }

            if (role != null && !role.trim().isEmpty()) {
                Role roleFilter = "admin".equalsIgnoreCase(role) ? Role.ROLE_ADMIN : Role.ROLE_USER;
                accounts = accounts.stream()
                        .filter(a -> a.getRole() == roleFilter)
                        .collect(Collectors.toList());
            }

            if (status != null && !status.trim().isEmpty()) {
                Boolean isActive = "active".equalsIgnoreCase(status);
                accounts = accounts.stream()
                        .filter(a -> a.isActivated() == isActive)
                        .collect(Collectors.toList());
            }

            model.addAttribute("accounts", accounts);

            if (username != null && !username.trim().isEmpty()) {
                Optional<Account> accountOptional = accountRepository.findById(username);
                if (accountOptional.isPresent()) {
                    Account account = accountOptional.get();
                    model.addAttribute("selectedAccount", account);

                    List<Order> orders = account.getOrders();
                    long totalOrders = orders.size();
                    double totalSpending = orders.stream()
                            .flatMap(order -> order.getOrderDetails().stream())
                            .filter(detail -> detail.getPrice() != null && detail.getQuantity() != null)
                            .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                            .sum();

                    Map<String, Object[]> productStatsMap = new HashMap<>();
                    for (Order order : orders) {
                        for (OrderDetail detail : order.getOrderDetails()) {
                            if (detail.getPrice() == null || detail.getQuantity() == null) continue;

                            String productName = detail.getProduct().getName();
                            int quantity = detail.getQuantity();
                            double totalPrice = detail.getPrice() * quantity;
                            LocalDateTime lastPurchaseDate = order.getCreateDate();

                            if (productStatsMap.containsKey(productName)) {
                                Object[] existingData = productStatsMap.get(productName);
                                int totalQuantity = (Integer) existingData[0] + quantity;
                                double totalSpent = (Double) existingData[1] + totalPrice;
                                LocalDateTime latestDate = ((LocalDateTime) existingData[2]).isAfter(lastPurchaseDate)
                                        ? (LocalDateTime) existingData[2] : lastPurchaseDate;
                                productStatsMap.put(productName, new Object[]{totalQuantity, totalSpent, latestDate});
                            } else {
                                productStatsMap.put(productName, new Object[]{quantity, totalPrice, lastPurchaseDate});
                            }
                        }
                    }

                    List<Map.Entry<String, Object[]>> topProducts = productStatsMap.entrySet().stream()
                            .sorted((p1, p2) -> Double.compare((Double) p2.getValue()[1], (Double) p1.getValue()[1]))
                            .limit(3)
                            .collect(Collectors.toList());

                    model.addAttribute("totalOrders", totalOrders);
                    model.addAttribute("totalSpending", totalSpending);
                    model.addAttribute("topProducts", topProducts);
                }
            }

            return "admin/accountManage";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách tài khoản: " + e.getMessage());
            e.printStackTrace();
            return "admin/accountManage";
        }
    }

    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<?> updateAccountStatus(@RequestParam("username") String username,
                                                 @RequestParam("isActive") boolean isActive) {
        try {
            Optional<Account> accountOptional = accountRepository.findById(username);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                account.setActivated(isActive);
                accountRepository.save(account);
                return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
            }
            return ResponseEntity.status(404).body(Map.of("error", "Tài khoản không tồn tại!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    @PostMapping("/delete/{username}")
    public String deleteAccount(@PathVariable("username") String username, RedirectAttributes redirectAttributes) {
        try {
            Optional<Account> accountOptional = accountRepository.findById(username);
            if (accountOptional.isPresent()) {
                accountRepository.delete(accountOptional.get());
                redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản không tồn tại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa tài khoản thất bại: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }
}