package com.myteam.backupback.mapper;

import com.myteam.backupback.entity.Admin;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Admin 数据库接口
 **/
public interface AdminMapper {

    int insert(Admin admin);

    int deleteById(Integer id);

    int updateById(Admin admin);

    Admin selectById(Integer id);

    List<Admin> selectAll(Admin admin);

    @Select("select * from admin where account = #{account}")
    Admin selectByAccount(String account);
}
