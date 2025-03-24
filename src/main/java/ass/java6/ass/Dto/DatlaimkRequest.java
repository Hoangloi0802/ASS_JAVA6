package ass.java6.ass.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DatlaimkRequest {
    @NotBlank(message = "mật khẩu không được để trống")
    @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String password;
    @NotBlank(message = "mật khẩu nhập lại không được để trống")
    @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String confirmPassword;
    public DatlaimkRequest() {
    }
    public DatlaimkRequest(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    
}
