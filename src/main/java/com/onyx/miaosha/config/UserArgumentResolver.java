package com.onyx.miaosha.config;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken=request.getParameter(MiaoshaUserService.COOK_NAME_TOKEN);
        String cookieToken=getCookiesValue(request,MiaoshaUserService.COOK_NAME_TOKEN);

        if(StringUtils.isBlank(cookieToken) && StringUtils.isBlank(paramToken)){
            return null;
        }
        //优先的是cookie中的
        String token=StringUtils.isBlank(cookieToken)?paramToken:cookieToken;
        //没有直接在controller中 从redis中获取user,还是定义了一个方法,为了分层的清晰
        MiaoshaUser user=miaoshaUserService.getByToken(token,response);

        return user;
    }

    private String getCookiesValue(HttpServletRequest request, String cookNameToken) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if(cookNameToken.equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
