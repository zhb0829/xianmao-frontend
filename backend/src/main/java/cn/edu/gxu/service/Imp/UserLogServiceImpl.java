package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.LoginResult;
import cn.edu.gxu.controller.dto.UserDto;
import cn.edu.gxu.dao.UserLogMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.UserLog;
import cn.edu.gxu.service.UserLogService;
import cn.edu.gxu.utils.TokenUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 使用mybatis-plus 首先每个mapper，service,imp都要绑定
 */
@Service
@Transactional
public class UserLogServiceImpl  extends ServiceImpl<UserLogMapper, UserLog> implements UserLogService {
    private static int UserLoginId;
    private static Log log = Log.get();
    @Resource
    private UserLogMapper mapper;
    @Override
    public LoginResult login(UserDto userDto) {
        userDto.setPassword(new MD5().digestHex(userDto.getPassword()));//实现md5加密
        UserLog one = getUserLogInfo(userDto);
        LoginResult loginResult = new LoginResult();
        if (one != null) {
            BeanUtil.copyProperties(one,loginResult, true);
            //设置tokeN
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            loginResult.setToken(token);
            return loginResult;
        } else {
            throw new ServiceException(Constants.CODE_500, "账号不存在或账号和密码不符合");
        }
    }

    private UserLog getUserLogInfo(UserDto userDto) {
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userDto.getEmail());//queryWrapper是个封装实体类
        queryWrapper.eq("password", userDto.getPassword());
        UserLog userLog;
        try {
            userLog = getOne(queryWrapper);

        } catch (Exception e) {
            log.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return userLog;

    }

    /**
     * @param userDto 表示前端传过来的数据
     * @return user
     */
    @Transactional
    @Override
    public UserLog register(UserDto userDto) {
        userDto.setPassword(MD5.create().digestHex(userDto.getPassword()));
        UserLog one = getUserLogInfo(userDto);
        if (one == null) {
            one = new UserLog();
            BeanUtil.copyProperties(userDto, one, true); //这个方法为hutoll工具包中的属性注入方法，就是两个类如果属性相同则不用
            one.setAuthority(0);//默认为普通用户，管理员用内置账户   //一一设置，同时ignorecase 没有属性就不注入
            try {
                mapper.insert(one);
            } catch (Exception e) {
                log.error(e);
                throw new ServiceException(Constants.CODE_500, e.toString());
            }
            return one;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已经存在");
        }
    }

    @Override
    public boolean updatePassWord(UserDto userDto) {

        UserLog userLog = new UserLog();
        userLog.setId(UserLoginId);
        userLog.setPassword(MD5.create().digestHex(userDto.getPassword()));
        try {
            mapper.updateByPrimaryKeySelective(userLog);
        }catch (Exception e){
            throw  new ServiceException(Constants.CODE_500,"系统出错了！！");
        }
        return  true;
    }

    @Override
    public void setUserLogId(int id) {
        this.UserLoginId =id;
    }

    @Override
    public UserLog getAccountById(int id) {
        return  mapper.selectByUserId(id);
    }



    @Override
    public UserLog selectUserLog() {
        return  mapper.selectByUserId(UserLoginId);

    }

    @Override
    public int SelectIdByAccount(String account) {
        return  mapper.selectIdByAccount(account);
    }

    @Override
    public boolean unsubscribe() {
        try {
           mapper.deleteByPrimaryKey(UserLoginId);
        }catch (Exception e){
            log.error(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"系统出错了");
        }
        return   true;
    }
}
