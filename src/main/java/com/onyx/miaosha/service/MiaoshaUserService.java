package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.MiaoshaUserDao;
import com.onyx.miaosha.domain.MiaohaUser;
import com.onyx.miaosha.exception.GlobalException;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.utils.MD5Util;
import com.onyx.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {


    @Autowired
    private MiaoshaUserDao miaoshaUserDao;


    public MiaohaUser getById(Long id){
        MiaohaUser user = miaoshaUserDao.getById(id);
        return user;
    }

    //public CodeMsg login(LoginVo loginVo) {
    public boolean login(LoginVo loginVo) {
        if(loginVo==null){
            //return CodeMsg.SERVER_ERROR;
            //这样返回不好,出现了异常直接抛出去
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        }
        //参数校验,手机号是否存在
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        MiaohaUser user = getById(Long.decode(mobile));
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
        return true;
    }
}
