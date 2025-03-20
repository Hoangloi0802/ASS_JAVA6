package ass.java6.ass.Config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
@Configuration
@EnableWebSecurity
public class SendEmailConfig {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to ,String subject ,String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public String generateOtp() {
    Random random = new Random();
    int otp = random.nextInt(999999); // Tạo một số ngẫu nhiên từ 0 đến 999999
    return String.format("%06d", otp); // Đảm bảo OTP có 6 chữ số
   }
 
}
