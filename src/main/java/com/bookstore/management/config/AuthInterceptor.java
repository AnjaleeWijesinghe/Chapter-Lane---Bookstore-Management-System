package com.bookstore.management.config;

import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public AuthInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && !sessionService.isAdminAuthenticated(request.getSession(false))) {
            response.sendRedirect("/login?required=admin");
            return false;
        }
        if ((uri.startsWith("/cart") || uri.startsWith("/orders") || uri.startsWith("/profile"))
                && !sessionService.isCustomerAuthenticated(request.getSession(false))) {
            response.sendRedirect("/login?required=customer");
            return false;
        }
        return true;
    }
}
