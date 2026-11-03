package cn.edu.gxu.service;

import cn.edu.gxu.model.Collect;
import cn.edu.gxu.model.CommodityInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.util.List;

public interface CollectService extends IService<Collect> {

    boolean insertCollect(Integer userId,Integer commodityId);
    int deleteCollect(Integer userId,Integer commodityId);
    List<CommodityInfo> selectCollect(Integer userId);

    long countCollect(int commodityId );

    boolean shopInsertCollect(Integer userId, Integer commodityId);
}
