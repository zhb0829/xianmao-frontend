package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.PersonDetailsDto;
import cn.edu.gxu.controller.dto.PersonInfoDto;
import cn.edu.gxu.dao.CommodityInfoMapper;
import cn.edu.gxu.dao.OrderMapper;
import cn.edu.gxu.dao.PersonInfoMapper;
import cn.edu.gxu.dao.UserInfoMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Order;
import cn.edu.gxu.model.PersonInfo;
import cn.edu.gxu.model.UserInfo;
import cn.edu.gxu.service.ImageToBase64;
import cn.edu.gxu.service.PersonInfoService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

@Transactional
@Service
public class PersonInfoServiceImpl extends ServiceImpl<PersonInfoMapper,PersonInfo> implements PersonInfoService {
    @Resource
     PersonInfoMapper personInfoMapper;
    @Resource
    CommodityInfoMapper commodityInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    OrderMapper orderMapper;
    @Resource
    ImageToBase64 imageToBase64;


    @Override
    public PersonInfoDto getPersonInfoDto(int id) {
        QueryWrapper<PersonInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid",id);
        PersonInfo personInfo = getOne(queryWrapper,true);
        PersonInfoDto personInfoDto = new PersonInfoDto();
        BeanUtil.copyProperties(personInfo,personInfoDto,true);
        return  personInfoDto;
    }

    @Override
    public PersonInfoDto savePerson(PersonInfoDto personInfoDto,int id) {


                PersonInfo  personInfo = new PersonInfo();
                personInfo = personInfoMapper.selectByUserId(id);
            if (personInfo!=null){
                if(personInfoDto.getTelephone()!="") personInfo.setTelephone(personInfoDto.getTelephone());
                if(personInfoDto.getAddress()!="") personInfo.setAddress(personInfoDto.getAddress());
                try {
                    UpdateWrapper<PersonInfo> queryWrapper = new UpdateWrapper<>();
                    queryWrapper.eq("userId",id);
                    update(personInfo,queryWrapper);
                }catch (Exception e){
                    log.error(e.getMessage());
                    throw new ServiceException(Constants.CODE_500,"系统出错了,");
                }
            }
            else {
                BeanUtil.copyProperties(personInfoDto,personInfo,true);
                try {
                    save(personInfo);
                }catch (Exception e){
                    log.error(e.getMessage());
                    throw new  ServiceException(Constants.CODE_500,"系统出错了,");
                }
            }
            return  personInfoDto;
        }
    @Override
    public Map<String, Object> getPersonNumber(Integer userid) {
        Map<String,Object> m = new HashMap<>();
        m.put("liked",commodityInfoMapper.getLiked(userid));
        m.put("bought",commodityInfoMapper.getBought(userid));
        m.put("sold",commodityInfoMapper.getSold(userid));
        m.put("published",commodityInfoMapper.getPulished(userid));
        String  chu =orderMapper.getSoldNum(userid);
        float rank;
        if (chu==null){
            rank =0;
        }
        else rank = Float.parseFloat( orderMapper.getBoughtNum(userid))/Float.parseFloat( orderMapper.getSoldNum(userid));
        m.put("rank", rank);
        return m;
    }

    @Override
    public List<CommodityInfo> getLiked(Integer userid) {
        try {
            List<CommodityInfo> com = new ArrayList<>();
            com = commodityInfoMapper.selectLiked(userid);
            for (CommodityInfo temp : com) {
                String base64 = imageToBase64.ImageToBase64(temp.getFirstPhoto());
                temp.setFirstPhoto(base64);
            }
            return com;
        }catch (Exception e){
            log.error(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"出错");
        }
    }

    @Override
    public List<PersonDetailsDto> getBought(Integer userid) {
        try{
        List<PersonDetailsDto> personDetailsDtos = new ArrayList<>();
        List<Order> com = commodityInfoMapper.selectBought(userid);
        for ( Order temp:com) {
            CommodityInfo commodityInfo = commodityInfoMapper.selectById(temp.getCommodityId());
            PersonDetailsDto personDetailsDto = new PersonDetailsDto();
            BeanUtil.copyProperties(temp,personDetailsDto,true);
            String base64 = imageToBase64.ImageToBase64(commodityInfo.getFirstPhoto());
            personDetailsDto.setName(commodityInfo.getTradeName());
            personDetailsDto.setPic(base64);
            personDetailsDto.setSeller(userInfoMapper.getName(temp.getSellUserId()));
            personDetailsDtos.add(personDetailsDto);
        }
        return personDetailsDtos;
    }catch (Exception e){
        log.error(e.getMessage());
        throw  new ServiceException(Constants.CODE_500,"出错");
    }
    }

    @Override
    public List<PersonDetailsDto> getSold(Integer userid) {
        try{
            List<PersonDetailsDto> personDetailsDtos = new ArrayList<>();
            List<Order> com = commodityInfoMapper.selectSold(userid);
            for ( Order temp:com) {
                CommodityInfo commodityInfo = commodityInfoMapper.selectById(temp.getCommodityId());
                PersonDetailsDto personDetailsDto = new PersonDetailsDto();
                BeanUtil.copyProperties(temp,personDetailsDto,true);
                String base64 = imageToBase64.ImageToBase64(commodityInfo.getFirstPhoto());
                personDetailsDto.setName(commodityInfo.getTradeName());
                personDetailsDto.setPic(base64);
                personDetailsDto.setSeller(userInfoMapper.getName(temp.getSellUserId()));
                personDetailsDtos.add(personDetailsDto);
            }
            return personDetailsDtos;
    }catch (Exception e){
        log.error(e.getMessage());
        throw  new ServiceException(Constants.CODE_500,"出错");
        }
    }

    @Override
    public List<CommodityInfo> getPublished(Integer userid) {
    try{
        List<CommodityInfo> com = new ArrayList<>();
        com = commodityInfoMapper.selectPulished(userid);
        for (CommodityInfo temp: com) {
            String base64 = imageToBase64.ImageToBase64(temp.getFirstPhoto());
            temp.setFirstPhoto(base64);
        }
        return com;
        }catch (Exception e){
        log.error(e.getMessage());
        throw  new ServiceException(Constants.CODE_500,"出错");
        }
    }
    @Override
    public List<Map<String, Object>> getAllCategory() {
        return personInfoMapper.selectAll();
    }

    @Override
    public boolean insertPerson(int userId) {
         PersonInfo personInfo = new PersonInfo();
         personInfo.setUserid(userId);
        if (personInfoMapper.insert(personInfo)==1)
            return true;
         else return false ;
    }


}
