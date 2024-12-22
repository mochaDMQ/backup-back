package com.myteam.backupback.service;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.Backup;
import com.myteam.backupback.entity.User;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.mapper.BackupMapper;
import com.myteam.backupback.mapper.UserMapper;
import com.myteam.backupback.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 业务处理
 **/

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private BackupMapper backupMapper;
    @Autowired
    private BackupService backupService;

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

    @Transactional
    public void deleteById(Integer id){
        List<Backup> userBackups = backupMapper.selectAll(id);
        User delUser = userMapper.selectById(id);
        File userPath = new File(Constants.PROJECT_PATH + "/uploads/" + delUser.getAccount());
        try{
            for(Backup bk:userBackups){
                String zipName = bk.getId() + "--" + bk.getBackupName() + ".zip";
                File zipFile = new File(userPath,zipName);
                zipFile.delete();
            }
            userPath.delete();
        }catch(Exception e){
            throw new RuntimeException("用户相关备份数据删除失败");
        }
        userMapper.deleteById(id);

    }

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


}
