package ass.java6.ass.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import ass.java6.ass.Service.SessionService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        Boolean isAdmin = sessionService.get("admin", false);
        String username = sessionService.get("username", "");
        if ((uri.startsWith("/admin") || uri.startsWith("/userManage") ||
                uri.startsWith("/category") || uri.startsWith("/statistics") ||
                uri.startsWith("/bill")) && !Boolean.TRUE.equals(isAdmin)) {
            System.out.println("Không có quyền truy cập admin!");
            response.sendRedirect("/login?error=accessDenied");
            return false;
        }
        if ((uri.startsWith("/cart") || uri.startsWith("/editProfile")) && (username == null || username.isEmpty())) {
            System.out.println("Chưa đăng nhập, chuyển hướng đến login!");
            response.sendRedirect("/login?error=notLoggedIn");
            return false;
        }
        return true;
    }
}
