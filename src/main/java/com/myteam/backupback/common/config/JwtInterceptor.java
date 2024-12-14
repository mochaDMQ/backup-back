package com.myteam.backupback.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.service.AdminService;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.myteam.backupback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Log log = LogFactory.get();

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            token = request.getParameter(Constants.TOKEN);
        } // 从header→参数获取TOKEN，没有就401，前端返回login
        if (ObjectUtil.isEmpty(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }
        AuthUser authUser = null;
        try {
            // 解析token获取存储的数据
            String userRole = JWT.decode(token).getAudience().get(0);
            String userId = userRole.split("-")[0];
            String role = userRole.split("-")[1];
            // 根据token携带的role和id，从admin或user中获取对应用户信息返回
            if (RoleEnum.ADMIN.name().equals(role)) {
                authUser = adminService.selectById(Integer.valueOf(userId));
            }else if(RoleEnum.USER.name().equals(role)){
                authUser = userService.selectById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        if (ObjectUtil.isNull(authUser)) { // token携带的用户不存在时
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        try { // 使用pwd作为密钥验证token，否则抛出异常
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(authUser.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        return true;
    }
}