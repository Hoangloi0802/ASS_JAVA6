package ass.java6.ass.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
public class DangkyRequest {
    @NotBlank(message = "tài khoản không được để trống")
    @Size(min = 6,message = "tài khoản phải trên 6 kí tự")
    private String username;
    @NotBlank(message = "mật khẩu không được để trống")
    @Size(min = 6,message =  "mật khẩu phải trên 6 kí tự")
    private String password;
    @NotBlank(message = "họ và tên không được để trống")
    private String fullname;
    @NotBlank(message = "email không được để trống")
    @Email(message = "email không đúng định dạng")
    private String email;
    @NotBlank(message = "số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại không đúng định dạng")
    private String mobile;
    public DangkyRequest() {
    }
    public DangkyRequest(
           String username,
           String password,
           String fullname,
           String email,
           String mobile) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.mobile = mobile;

    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    
    
    
    
    
}
