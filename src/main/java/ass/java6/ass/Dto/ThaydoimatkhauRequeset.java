package ass.java6.ass.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ThaydoimatkhauRequeset {
    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
     @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String currentPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String newPassword;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    @Size(min = 6 ,message = "mật khẩu phải trên 6 kí tự")
    private String confirmPassword;

    public ThaydoimatkhauRequeset() {
    }

    public ThaydoimatkhauRequeset(
            String currentPassword,
            String newPassword,
            String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    
}
