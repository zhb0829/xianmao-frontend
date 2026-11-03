package cn.edu.gxu.controller;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.Basic.Result;
import cn.edu.gxu.controller.dto.LoginResult;
import cn.edu.gxu.controller.dto.UserDto;
import cn.edu.gxu.controller.dto.UserInfoDto;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.UserLog;
import cn.edu.gxu.service.MailService;
import cn.edu.gxu.service.PersonInfoService;
import cn.edu.gxu.service.UserInfoService;
import cn.edu.gxu.service.UserLogService;
import cn.edu.gxu.utils.TokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.Web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserLogController {
    @Resource
    MailService mailService;
    @Resource
    private UserLogService userLogService;
    @Resource
    UserInfoService userInfoService;
    @Resource
    PersonInfoService personInfoService;

    @PostMapping("/register")
    public Result register(UserDto userDto, HttpSession httpSession) {
        // 昵称
        String nickname = userDto.getAccount();
        // 密码
        String password = userDto.getPassword();
        // 邮箱，这里把邮箱作为账户传入
        String account = userDto.getEmail();
        if (mailService.registered(userDto, httpSession)) {
            return Result.error(Constants.CODE_400, "验证码不匹配或者邮箱已经被绑定了");
        }
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.error(Constants.CODE_401, "参数错误"  );
        } else {
//            存log
            userLogService.register(userDto);

//            存info
            QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", userDto.getEmail());
            UserLog ud = userLogService.getOne(queryWrapper);
            UserInfoDto userInfoDto = new UserInfoDto();
            //ud.getId就是用户id
            userInfoDto.setId(ud.getId());
            personInfoService.insertPerson(ud.getId());
            userInfoDto.setNickname(nickname);
            userInfoService.infoRegister(userInfoDto);
            return Result.success();
//            return Result.error(Constants.CODE_401, "未能新增用户");
        }
}

    @GetMapping("/getCode")
    public Result getCode(String email, HttpSession httpSession) {
        if (StringUtils.isBlank(email)) {
            return Result.error(Constants.CODE_401, "邮箱格式错误");
        }
        if (!mailService.hadMail(email)) {
            return Result.error(Constants.CODE_401, "邮箱已被占用");
        }
        mailService.sendMimeMail(email, httpSession);
        return Result.success();
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestParam() String account, @RequestParam() String pwd) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(pwd)) {
             throw  new ServiceException(Constants.CODE_400,"账号或密码为空");
        }
        UserDto userDto = new UserDto();
        userDto.setAccount(account);
        userDto.setEmail(account);
        userDto.setPassword(pwd);
        LoginResult loginResult = userLogService.login(userDto);
        Map<String, Object> map = new HashMap<>();
        map.put("id",loginResult.getId());
        map.put("token",loginResult.getToken());
        map.put("authority",loginResult.getAuthority());
        return  map ;
    }

    /**
     *
     * @param pwd 修改密码
     * @param confirm 确认修改后的密码
     * @return 返回结果
     */
    @PostMapping("/update")
    public Result updatePassword(String pwd, String confirm) {
        int  userid = TokenUtils.getCurrentUser().getId();
        userLogService.setUserLogId(userid);
        if (StringUtils.isBlank(pwd) || StringUtils.isBlank(confirm)) {
            return Result.error(Constants.CODE_400, "密码或确认密码不能为空");
        }
        if (!pwd.equals(confirm)) {
            return Result.error(Constants.CODE_400, "输入密码不一致");
        }
        UserDto userDto = new UserDto();
        userDto.setPassword(pwd);
        userLogService.updatePassWord(userDto);
        return Result.success();
    }

    /*
     注销账户
     */
    @GetMapping("/unsubscribe")
    public Result unsubscribe() {
        int  userid = TokenUtils.getCurrentUser().getId();
        userLogService.setUserLogId(userid);
        userLogService.unsubscribe();
        return Result.success();
    }

}
