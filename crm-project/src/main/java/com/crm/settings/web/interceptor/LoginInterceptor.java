package com.crm.settings.web.interceptor;

import com.crm.commons.contants.Contants;
import com.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 登陆验证，判断session中有没有user对象
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        if (user == null){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()); // 手动重定向时，url必须添加项目的名称
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
