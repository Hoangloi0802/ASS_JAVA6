package ass.java6.ass.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DangnhapRequest {
    @NotBlank(message = "tài khoản không được để trống")
    @Size(min = 6,message = "tài khoản phải trên 6 kí tự")
    private String username;
    @NotBlank(message = "mật khẩu không được để trống")
    @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String password;
    public DangnhapRequest() {
    }
    public DangnhapRequest( String username, String password) {
        this.username = username;
        this.password = password;
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
    
}
