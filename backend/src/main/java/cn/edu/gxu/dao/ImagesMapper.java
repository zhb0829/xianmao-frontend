package cn.edu.gxu.dao;

import cn.edu.gxu.model.Images;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface ImagesMapper extends BaseMapper<Images> {
    int deleteByPrimaryKey(Integer id);

    int insert(Images record);

    int insertSelective(Images record);

    Images selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Images record);

    int updateByPrimaryKey(Images record);
}