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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để dễ debug
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/dangky", "/dangnhap", "/css/**", "/js/**").permitAll() 
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") 
                        .requestMatchers("/user/**").hasAuthority("USER") 
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/dangnhap") 
                        .loginProcessingUrl("/process-login") 
                        .defaultSuccessUrl("/home", true) 
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") 
                        .logoutSuccessUrl("/dangnhap?logout") 
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
