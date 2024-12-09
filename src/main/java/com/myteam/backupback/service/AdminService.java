package com.myteam.backupback.service;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.Admin;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.mapper.AdminMapper;
import com.myteam.backupback.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员实际业务处理
 **/
@Service
public class AdminService {
    @Resource
    private AdminMapper adminMapper;

    public void add(Admin admin){
        Admin dba = adminMapper.selectByAccount(admin.getAccount());
        if(ObjectUtil.isNotNull(dba)){ // 用户存在
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }

        if (ObjectUtil.isEmpty(admin.getPassword())) { // 管理员端新增使用默认密码
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }

        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getAccount());
        }

        admin.setRole(RoleEnum.ADMIN.name());
        adminMapper.insert(admin);
    }

    public void deleteById(Integer id){ adminMapper.deleteById(id); }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adminMapper.deleteById(id);
        }
    }

    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }

    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }

    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    // 分页查询
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    public AuthUser login(AuthUser authUser){
        AuthUser dba = adminMapper.selectByAccount(authUser.getAccount());
        if (ObjectUtil.isNull(dba)) { // 用户不存在
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!authUser.getPassword().equals(dba.getPassword())) { // 密码错误
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dba.getId()+"-"+RoleEnum.ADMIN.name();
        String token = TokenUtils.createToken(tokenData,dba.getPassword());
        dba.setToken(token);
        return dba;
    }

    public void register(AuthUser authUser){
        Admin admin = new Admin();
        BeanUtils.copyProperties(authUser, admin);
        add(admin);
    }

    public void updatePassword(AuthUser authuser){
        Admin dba = adminMapper.selectByAccount(authuser.getAccount());
        if (ObjectUtil.isNull(dba)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!authuser.getPassword().equals(dba.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dba.setPassword(authuser.getNewPassword());
        adminMapper.updateById(dba);
    }



}
