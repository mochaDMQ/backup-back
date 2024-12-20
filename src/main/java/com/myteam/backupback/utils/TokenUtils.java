package com.myteam.backupback.utils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.service.AdminService;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.myteam.backupback.service.UserService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

// 使用slf4j进行TOKEN生成日志记录
public class TokenUtils {

    private static final Log log = LogFactory.get();
    private static AdminService staticAdminService;
    private static UserService staticUserService;

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @PostConstruct
    public void setUserService(){ // static方法中使用非static变量，需要通过@PostConstruct注解初始化
        staticAdminService = adminService;
        staticUserService = userService;
    }

    // 使用JWT生成登录TOKEN
    public static String createToken(String data, String sign) {
        Date expirationDate = DateUtil.offsetHour(new Date(), 2);
        System.out.println(DateUtil.formatDate(expirationDate));
        log.info("token过期时间: {}", expirationDate);
        return JWT.create().withAudience(data) // 将 userId-role 保存到 token 里面,作为载荷
                .withExpiresAt(expirationDate) // token过期时间
                .sign(Algorithm.HMAC256(sign)); // 以 pwd 作为 token 的密钥
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
                } else if (RoleEnum.USER.name().equals(role)) {
                    return staticUserService.selectById(Integer.valueOf(userId));
                }
            }
        }catch(Exception e){
            log.error("获取用户信息失败",e);
        }
        return new AuthUser();
    }
}
