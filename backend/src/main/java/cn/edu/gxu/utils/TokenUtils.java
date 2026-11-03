package cn.edu.gxu.utils;

import cn.edu.gxu.model.UserLog;
import cn.edu.gxu.service.UserLogService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2Log;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.stereotype.Component;
import org.springframework.Web.context.request.RequestContextHolder;
import org.springframework.Web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {

    private static UserLogService staticUserService;

    @Resource
    private UserLogService userLogService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userLogService;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String genToken(String userId, String sign) {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 10)) // 10小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息`
     *
     * @return user对象
     */
    public static UserLog getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token =request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                Log log = new Log4j2Log("token");
                log.error(request.getRequestURI());
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
