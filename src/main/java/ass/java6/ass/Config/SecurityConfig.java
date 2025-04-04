package ass.java6.ass.Config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String[] PUBLIC_ENDPOINTS = {
        "/", "/Dangnhap", "/Dangky",
        "/profile/update","/profile",
        "/donmua", "/donhang", "/chitietdonhang/**", "/shop",
        "/giohang/**", "/thanhtoan/**", "/product/**",
        "/cart/**",
        "/quenmk", "/datlaimk", "/checkotp","/otpquenmk","/datlaimk","/datlaimatkhau","/goilaiotp",
        "/uploads/**","/admin/products/**",

            "/css/**", "/js/**", "/img/**", "/bootstrap-5.3.3/dist/**", "/fonts/**", "/logout", "/doimk",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Các trang công khai
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Chỉ admin mới truy cập được trang
                                                                                 // admin
                        .requestMatchers("/**").hasAuthority("ROLE_ADMIN") // Admin có quyền truy cập tất cả trang
                        .anyRequest().denyAll() // User bị chặn nếu cố truy cập trang khác
                )
                .formLogin(form -> form
                        .loginPage("/Dangnhap")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/shop", true)
                        .failureHandler(customFailureHandler())
                        .permitAll()
                        .successHandler((req, res, auth) -> {
                            String username = req.getParameter("username");

                            String sql = "SELECT username, email, fullname, role FROM accounts WHERE username = ?";
                            Account currentUser = jdbcTemplate.queryForObject(sql, new Object[] { username },
                                    (rs, rowNum) -> {
                                        Account acc = new Account();
                                        acc.setUsername(rs.getString("username"));
                                        acc.setEmail(rs.getString("email"));
                                        acc.setFullname(rs.getString("fullname"));
                                        String rolestr = rs.getString("role");
                                        if (rolestr != null && !rolestr.startsWith("ROLE_")) {
                                            rolestr = "ROLE_" + rolestr;
                                        }
                                        acc.setRole(Role.valueOf(rolestr));
                                        return acc;
                                    });

                            if (currentUser != null) {
                                req.getSession().setAttribute("currentUser", currentUser);
                                req.getSession().setAttribute("username",
                                        currentUser.getFullname() != null ? currentUser.getFullname()
                                                : currentUser.getEmail());
                                req.getSession().setAttribute("role", currentUser.getRole().name()); // 👈 Lưu quyền vào
                                                                                                     // session

                                res.sendRedirect("/shop");
                            } else {
                                res.sendRedirect("/Dangnhap?error=user_not_found");
                            }
                        }))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/Dangnhap?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<Account> optionalAccount = accountRepository.findByUsername(username);
            if (optionalAccount.isEmpty()) {
                throw new BadCredentialsException("tài khoản không tồn tại");
            }
            Account account = optionalAccount.get();
            if (account.isActivated() != true) {
                throw new DisabledException("tài khoản chưa được kích hoạt vui lòng liên hệ admin");
            }
            return account;
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String error = "true"; // Mặc định
            if (exception instanceof BadCredentialsException) {
                error = "userNotFound";
            } else if (exception instanceof DisabledException) {
                error = "notActivated";
            }
            response.sendRedirect("/Dangnhap?error=" + error);
        };
    }

}