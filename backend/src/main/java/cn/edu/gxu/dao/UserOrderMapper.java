    package cn.edu.gxu.dao;

    import cn.edu.gxu.model.Order;
    import com.baomidou.mybatisplus.core.mapper.BaseMapper;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    public interface UserOrderMapper extends BaseMapper<Order> {

        List<Order> selectUserID(Integer userId);

        List<Order> selectSellUserID(Integer sellUserId);

    }
