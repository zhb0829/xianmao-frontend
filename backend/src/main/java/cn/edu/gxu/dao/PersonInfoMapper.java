package cn.edu.gxu.dao;

import cn.edu.gxu.model.PersonInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface PersonInfoMapper extends BaseMapper<PersonInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(PersonInfo record);

    int insertSelective(PersonInfo record);

    PersonInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PersonInfo record);

    int updateByPrimaryKey(PersonInfo record);
    PersonInfo selectByUserId(Integer userId);

    @Select("select id,category as name from category")
    List<Map<String, Object>> selectAll();
    @Select("select category from category")
    LinkedList<String> selectAllCategory();
}