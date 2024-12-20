package com.myteam.backupback.service;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.User;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.mapper.UserMapper;
import com.myteam.backupback.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务处理
 **/

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public void add(User user){
        User dbuser = userMapper.selectByAccount(user.getAccount());
        if(ObjectUtil.isNotNull(dbuser)){ // 用户存在
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }

        if (ObjectUtil.isEmpty(user.getPassword())) { // admin端默认处理
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }

        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getAccount());
        }
        userMapper.insert(user);
    }

    public void deleteById(Integer id){ userMapper.deleteById(id); }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id);
        }
    }

    public void updateById(User user) {
        userMapper.updateById(user);
    }

    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    // 分页模糊查询
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    public AuthUser login(AuthUser authUser){
        AuthUser dbuser = userMapper.selectByAccount(authUser.getAccount());
        if (ObjectUtil.isNull(dbuser)) { // 用户不存在
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!authUser.getPassword().equals(dbuser.getPassword())) { // 密码错误
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbuser.getId()+"-"+RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData,dbuser.getPassword());
        dbuser.setToken(token);
        return dbuser;
    }

    public void register(AuthUser authUser){
        User user = new User();
        BeanUtils.copyProperties(authUser, user);
        add(user);
    }

    public void updatePassword(AuthUser authuser){
        User dba = userMapper.selectByAccount(authuser.getAccount());
        if (ObjectUtil.isNull(dba)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!authuser.getPassword().equals(dba.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dba.setPassword(authuser.getNewPassword());
        userMapper.updateById(dba);
    }

}
