package ass.java6.ass.Config;

import java.beans.Customizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
   @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF (nếu cần)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( "/Dangnhap","/shop" , "/", "/Dangky", "/css/**", "/js/**","/img/**","/bootstrap-5.3.3/dist/**","/fonts/**","/giohang","/chitiet","/thanhtoan","/admin/categories/**","/admin/account/**","/admin/products/**","/admin/bill/**","/admin/statistics").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Chỉ Admin truy cập được
                .requestMatchers("/user/**").hasAuthority("ROLE_USER") // Chỉ User truy cập được
                .anyRequest().authenticated() // Còn lại yêu cầu đăng nhập
            )
            .formLogin(login -> login
                .loginPage("/dangnhap") // Trang login
                .loginProcessingUrl("/process-login") // Xử lý login
                .defaultSuccessUrl("/home", true) // Sau khi login thành công
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/dangnhap?logout") // Sau khi logout
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}

}
