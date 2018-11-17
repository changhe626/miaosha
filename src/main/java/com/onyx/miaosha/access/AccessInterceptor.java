package com.onyx.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.redis.AccessKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            MiaoshaUser user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod method = (HandlerMethod) handler;
            AccessLimit limit = method.getMethodAnnotation(AccessLimit.class);
            if (limit == null) {
                return true;
            }
            int maxCount = limit.maxCount();
            boolean login = limit.needLogin();
            int seconds = limit.seconds();
            String key = request.getRequestURI();
            if (login) {
                if (user == null) {
                    render(response, CodeMsg.NO_USER);
                    return false;
                }
                key += "_" + user.getId();
            } else {


            }
            //查询访问次数
            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey, key, Integer.class);
            if (count == null || count.compareTo(0) == 0) {
                redisService.set(accessKey, key, 1);
            } else {
                if (count < maxCount) {
                    redisService.incr(accessKey, key);
                } else {
                    render(response, CodeMsg.ACCESS_LIMIT);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 返回响应
     */
    private void render(HttpServletResponse response, CodeMsg msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream stream = response.getOutputStream();
        String string = JSON.toJSONString(Result.fail(msg));
        stream.write(string.getBytes("UTF-8"));
        stream.flush();
        stream.close();
    }


    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(MiaoshaUserService.COOK_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOK_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return miaoshaUserService.getByToken(token, response);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
