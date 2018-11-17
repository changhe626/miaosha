package com.onyx.miaosha.filter;


import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFilter  {
//implements Filter
    /*@Autowired
    private MiaoshaUserService miaoshaUserService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        *//*HttpServletRequest  r = (HttpServletRequest) request;
        String token=null;
        String parameter = r.getParameter("token");
        Cookie[] cookies = r.getCookies();
        for (Cookie cookie : cookies) {
            if("token".equals(cookie.getName())){
                token=cookie.getValue();
            }
        }
        if(token==null){
            token=parameter;
        }
        if(token==null){
            //都没有cookie直接放行过去
            chain.doFilter(request,response);
        }else{
            MiaoshaUser user=miaoshaUserService.getByToken(token,(HttpServletResponse) response);
            if(user!=null){
                chain.doFilter(request,response);
            }else{
                request.getRequestDispatcher("login/to_login");
            }
        }*//*
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }*/
}
