package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.LoginResult;
import cn.edu.gxu.controller.dto.UserDto;
import cn.edu.gxu.model.UserLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


@Service


public interface UserLogService extends IService<UserLog> {
    LoginResult login(UserDto userDto);
    UserLog register(UserDto userDto);
    boolean updatePassWord(UserDto userDto);
    boolean unsubscribe();
    int  SelectIdByAccount(String account);
    UserLog selectUserLog ();
    UserLog getAccountById(int id );
    void  setUserLogId(int id);
}
