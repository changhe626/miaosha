package com.onyx.miaosha.dao;

import com.onyx.miaosha.domain.MiaohaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    MiaohaUser  getById(@Param("id")Long id);


}
