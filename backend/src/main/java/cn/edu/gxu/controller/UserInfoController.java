package cn.edu.gxu.controller;

import cn.edu.gxu.Basic.Result;
import cn.edu.gxu.controller.dto.PersonInfoDto;
import cn.edu.gxu.controller.dto.UserInfoDto;
import cn.edu.gxu.controller.dto.PersonUpdateDto;
import cn.edu.gxu.service.Base64ToImage;
import cn.edu.gxu.service.PersonInfoService;
import cn.edu.gxu.service.UserInfoService;
import cn.edu.gxu.utils.TokenUtils;
import org.springframework.Web.bind.annotation.*;


import javax.annotation.Resource;

@RestController
@RequestMapping("/personal")
public class UserInfoController {
    @Resource
    UserInfoService userInfoService;
    @Resource
    PersonInfoService  personInfoService;
    @Resource
    Base64ToImage   base64ToImage;
    @PostMapping("/savePersonal")
    public Result savePersonal (@RequestBody PersonUpdateDto p){
        PersonInfoDto personInfoDto = new PersonInfoDto();
        personInfoDto.setAddress(p.getAddress());
        personInfoDto.setTelephone(p.getTelephone());
        personInfoDto.setName("");
        p.setId(TokenUtils.getCurrentUser().getId());
        UserInfoDto userInfoDto = new UserInfoDto();
        String imagePath ="project\\src\\main\\resources\\static\\avatar"+"\\"+p.getId()+"\\avatar.jpg";
        base64ToImage.Base64ToAvatar( imagePath,p.getAvatar());
        userInfoDto.setAvatar(imagePath);
        userInfoDto.setNickname(p.getNickname());
        personInfoService.savePerson(personInfoDto,p.getId());
        userInfoService.savaPersonInfo(userInfoDto,p.getId());
        return Result.success();
    }
    @GetMapping("/getPersonInfo")
    public  PersonInfoDto personInfoDto (){
        int  userid = TokenUtils.getCurrentUser().getId();
        return  personInfoService.getPersonInfoDto(userid);
    }
    @GetMapping("/getUserInfo")
  public  UserInfoDto userInfoDto(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return  userInfoService.getUserInfo(userid);
    }

}