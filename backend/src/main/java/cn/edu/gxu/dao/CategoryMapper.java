package cn.edu.gxu.dao;

import cn.edu.gxu.model.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CategoryMapper  extends BaseMapper<Category> {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}