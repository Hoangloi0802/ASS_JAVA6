package ass.java6.ass.Config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Configuration
@EnableWebSecurity
public class SendEmailConfig {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
    
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Có thể log thêm hoặc throw lỗi tùy bạn
        }
    }

    public String generateOtpEmailContent(String otpCode, String link) {
        return "<div style=\"max-width: 23rem; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; font-family: Arial, sans-serif;\">"
                + "<h2 style=\"text-align: center;\">Mã xác nhận OTP</h2>"

                + "<p style=\"text-align: center; color: #555;\">Vui lòng nhập mã OTP.</p>"
                + "<div style=\"text-align: center; margin: 20px 0;\">"
                + "<span style=\"display: inline-block; font-size: 24px; letter-spacing: 8px; font-weight: bold;\">"
                + otpCode + "</span>"
                + "</div>"
                + "<a href=\"" + link
                + "\" style=\"display: block; text-align: center; background-color: #000; color: #fff; padding: 10px; text-decoration: none; border-radius: 4px;\">Nhập mã OTP</a>"
                + "<p style=\"text-align: center; margin-top: 20px; color: #888;\">Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>"
                + "</div>";
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(999999); 
        return String.format("%06d", otp); 
    }

}
