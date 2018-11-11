package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.MiaoshaUserDao;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.exception.GlobalException;
import com.onyx.miaosha.redis.MiaoshaUserKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.utils.MD5Util;
import com.onyx.miaosha.utils.UUIDUtil;
import com.onyx.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOK_NAME_TOKEN="token";


    @Autowired
    private MiaoshaUserDao miaoshaUserDao;
    @Autowired
    private RedisService redisService;


    public MiaoshaUser getById(Long id){
        MiaoshaUser user = miaoshaUserDao.getById(id);
        return user;
    }

    //public CodeMsg login(LoginVo loginVo) {
    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo==null){
            //return CodeMsg.SERVER_ERROR;
            //这样返回不好,出现了异常直接抛出去
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        }
        //参数校验,手机号是否存在
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        MiaoshaUser user = getById(Long.decode(mobile));
        if(user==null){
            throw new GlobalException(CodeMsg.NO_USER);
            //return CodeMsg.NO_USER;
        }
        String pass = MD5Util.formPassToDBPass(password, user.getSalt());
        if(!user.getPassword().equals(pass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
            //return CodeMsg.PASSWORD_ERROR;
        }
        //return CodeMsg.SUCCESS;

        //登陆成功后生成token
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token,token,user);

        //生成cookie ,写出去
        addCookie(response, token);

        return true;
    }

    /**
     * 创建cookie对象
     * @param response
     * @param token
     */
    private void addCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOK_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    public MiaoshaUser getByToken(String token, HttpServletResponse response) {
        //一定要做参数的校验
        if(StringUtils.isBlank(token)){
            return null;
        }
        //直接从redis中取出来,
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //每次登陆生成新的cookie,保证每次登陆都更新新的cookie ,延长有效期
        addCookie(response,token);
        return user;
    }
}
