package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.UserInfoDto;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.PersonInfo;
import cn.edu.gxu.model.UserInfo;
import cn.edu.gxu.service.ImageToBase64;
import cn.edu.gxu.service.UserInfoService;
import cn.edu.gxu.dao.UserInfoMapper;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper,UserInfo> implements UserInfoService {
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    ImageToBase64 imageToBase64;
    @Override
    public UserInfoDto getUserInfo(int userid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid",userid);
        UserInfo userInfo = getOne(queryWrapper,true);
        UserInfoDto userInfoDto =new UserInfoDto();
        BeanUtil.copyProperties(userInfo,userInfoDto,true);
        userInfoDto.setAvatar(imageToBase64.ImageToBase64(userInfoDto.getAvatar()));
        return  userInfoDto;
    }

    @Override
    public boolean infoRegister(UserInfoDto u) {
//        先拿到变量再新建
        int uid = u.getId();
        String av = "";
        String nickName = u.getNickname();
        UserInfo ui = new UserInfo();
        ui.setUserid(uid);
        ui.setAvatar(av);
        ui.setNickname(nickName);
        return userInfoMapper.insert(ui) == 1;
    }

    @Transactional
    @Override
    public UserInfoDto savaPersonInfo(UserInfoDto userInfoDto,int id) {

        if (userInfoDto!=null){
            try {
                UserInfo userInfo = new UserInfo();
                QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("UserID",id);
                userInfo = getOne(queryWrapper);
                if(userInfoDto.getNickname()!="")userInfo.setNickname(userInfoDto.getNickname());
                if(userInfoDto.getAvatar()!="")userInfo.setAvatar(userInfoDto.getAvatar());
                update(userInfo,queryWrapper);
            }catch (Exception e){
                log.error(e.getMessage());
                throw new  ServiceException(Constants.CODE_500,"系统出错了,");
            }
        }
        else {
                throw new  ServiceException(Constants.CODE_400,"资料不能为空,");

        }
        return  userInfoDto;
    }


}
