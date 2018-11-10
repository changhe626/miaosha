package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.UserDao;
import com.onyx.miaosha.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(int id){
        return userDao.getUser(id);
    }

    @Transactional
    public void insert(){
        User user = new User();
        user.setId(2);
        user.setName("hah");
        int insert = userDao.insert(user);
        System.out.println(insert);

        User user2 = new User();
        user2.setId(1);
        user2.setName("hah2222");
        userDao.insert(user2);

    }


}
