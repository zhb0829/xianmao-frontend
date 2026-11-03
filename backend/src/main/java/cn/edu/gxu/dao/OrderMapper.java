package cn.edu.gxu.dao;

import cn.edu.gxu.model.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface OrderMapper extends BaseMapper<Order> {

    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    String getBoughtNum(Integer userid);
    String getSoldNum(Integer userid);


    @Select("select *\n" +
            "from `order` a\n" +
            "where  a.ID = #{id}")
    Order selectBy_Id(Integer id);

    @Update("UPDATE `order`\n" +
            "set LogisticsStatus = '确认收货'\n" +
            "where  ID =#{id}")
    boolean updateBy_Id(Integer id);

    @Update("UPDATE `order`\n" +
            "set LogisticsStatus = '已发货'\n" +
            "where  ID =#{id}")
    boolean updateBy_Idd(Integer id);
    @Select("select count(*)\n" +
            "from  collect \n" +
            "where  UserID = #{uid} and CommodityID = #{cid}")
    Integer selectByUidAndCid(Integer uid,Integer cid);
    @Select("select count(*)\n" +
            "from  `order`\n" +
            "where  SubmitTime < #{tempDate}")
    Integer getDateNum(Date tempDate);
}