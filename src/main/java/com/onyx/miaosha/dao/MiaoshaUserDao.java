package com.onyx.miaosha.dao;

import com.onyx.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    MiaoshaUser  getById(@Param("id")Long id);


    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoshaUser miaoshaUser);
}
