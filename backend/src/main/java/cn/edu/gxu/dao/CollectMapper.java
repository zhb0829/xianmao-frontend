package cn.edu.gxu.dao;

import cn.edu.gxu.model.Collect;
import cn.edu.gxu.model.CommodityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectMapper extends BaseMapper<Collect> {
    int insertCollect(Integer userId,Integer commodityId);
    int deleteCollect(Integer userId,Integer commodityId);
    List<CommodityInfo> selectCollect(Integer userId);
}
