package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.UserInfoDto;
import cn.edu.gxu.model.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/*
        没有删除只有修改和添加，当这张表要删除时，就是userLog这张表被删除，级联删除
 */
@Service
public interface UserInfoService extends IService<UserInfo> {
     UserInfoDto savaPersonInfo(UserInfoDto userInfoDto,int id);
     UserInfoDto getUserInfo(int id);

     boolean infoRegister(UserInfoDto u);
}
