package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.PersonDetailsDto;
import cn.edu.gxu.controller.dto.PersonInfoDto;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.PersonInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface  PersonInfoService extends IService<PersonInfo> {
        PersonInfoDto savePerson(PersonInfoDto personInfoDto,int id);
        PersonInfoDto getPersonInfoDto(int id);
        Map<String,Object> getPersonNumber(Integer userid);

        List<CommodityInfo> getLiked(Integer userid);

        List<PersonDetailsDto> getBought(Integer userid);

        List<PersonDetailsDto> getSold(Integer userid);

        List<CommodityInfo> getPublished(Integer userid);
        List<Map<String, Object>> getAllCategory();
        boolean  insertPerson(int userId);



}
