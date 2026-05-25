package com.ambow.springboot_adoption.interceptor;

import com.ambow.springboot_adoption.dao.UserMapper;
import com.ambow.springboot_adoption.vo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class NoLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/user/login")||requestURI.contains("/user/register")) {
            return true;
        }
        if (user == null||userMapper.getUserByUserName(user.getUserName())==null) {

            response.sendRedirect("http://localhost:5173/login");
            return false;
        }

        return true;
    }
}
