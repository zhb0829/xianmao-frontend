package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.dao.CollectMapper;
import cn.edu.gxu.dao.CommodityInfoMapper;
import cn.edu.gxu.dao.OrderMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.Collect;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Order;
import cn.edu.gxu.service.CollectService;
import cn.edu.gxu.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class CollectServiceImp extends ServiceImpl<CollectMapper,Collect> implements CollectService  {

    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public boolean insertCollect(Integer userId, Integer orderId) {
        Order order = orderMapper.selectBy_Id(orderId);
        int commodityId = order.getCommodityId();
        if (orderMapper.selectByUidAndCid(userId,commodityId)==0){
            collectMapper.insertCollect(userId,commodityId);
            return true;
        }
        return false;
    }

    @Override
    public int deleteCollect(Integer userId, Integer commodityId) {
        return collectMapper.deleteCollect(userId,commodityId);
    }

    @Override
    public long countCollect(int commodityId) {
        QueryWrapper<Collect>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commodityId",commodityId);
        return  count(queryWrapper);
    }

    @Override
    public boolean shopInsertCollect(Integer userId, Integer commodityId) {
        try {
            if (orderMapper.selectByUidAndCid(userId, commodityId) == 0) {
                collectMapper.insertCollect(userId, commodityId);
                return true;
            } else {
                collectMapper.deleteCollect(userId, commodityId);

            }return false;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ServiceException(Constants.CODE_401,"收藏失败");
        }
    }

    @Override
    public List<CommodityInfo> selectCollect(Integer userId) {
        return collectMapper.selectCollect(userId);
    }
}
