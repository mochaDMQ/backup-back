package com.myteam.backupback.utils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

// 使用slf4j进行TOKEN生成日志记录
public class TokenUtils {

    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);
    private static AdminService staticAdminService;

    @Resource
    AdminService adminService;

    @PostConstruct
    public void setUserService(){staticAdminService = adminService;}

    // 使用JWT生成登录TOKEN
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 将 userId-role 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    // 获取当前登录的用户信息
    public static AuthUser getCurrentUser(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            if(ObjectUtil.isNotEmpty(token)){
                // tokenData: id-role
                String tokenData = JWT.decode(token).getAudience().get(0);
                String userId = tokenData.split("-")[0];
                String role = tokenData.split("-")[1];
                if(RoleEnum.ADMIN.name().equals(role)){
                    return staticAdminService.selectById(Integer.valueOf(userId));
                }
            }
        }catch(Exception e){
            log.error("获取用户信息失败",e);
        }
        return new AuthUser();
    }
}
