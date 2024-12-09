package com.myteam.backupback.mapper;

import com.myteam.backupback.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * User 数据库接口
 **/
public interface UserMapper {

    int insert(User user);

    int deleteById(Integer id);

    int updateById(User user);

    User selectById(Integer id);

    List<User> selectAll(User user);

    @Select("select * from user where account = #{account}")
    User selectByAccount(String account);
}