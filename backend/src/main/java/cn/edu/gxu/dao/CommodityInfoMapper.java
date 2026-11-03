package cn.edu.gxu.dao;

import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface CommodityInfoMapper extends BaseMapper<CommodityInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(CommodityInfo record);

    int insertSelective(CommodityInfo record);

    CommodityInfo selectByPrimaryKey(Integer id);

    LinkedList<CommodityInfo>  selectSelective(String tradeName);
    int updateByPrimaryKeySelective(CommodityInfo record);

    int updateByPrimaryKey(CommodityInfo record);


    /*
        查询所有的商品的集合以及，收藏个数
        String 是商品名字
        object 是商品对象
     */
    @Select("select collect.CommodityID,commodityinfo.TradeName,commodityinfo.Price,commodityinfo.FirstPhoto,count(CommodityID) as 'collections' \n" +
            "from collect ,CommodityInfo\n" +
            " where collect.CommodityID=commodityinfo.ID \n" +
            " GROUP BY CommodityID")
    List<Map<String,Object>> selectAll();

    /*
       根据商品userid 查询商品名字和商品对象
     */

    @Select("select collect.CommodityID,commodityinfo.TradeName,commodityinfo.Price,commodityinfo.FirstPhoto,commodityinfo.Description,commodityinfo.ShippingAddress,count(CommodityID) as 'collections' \n" +
            "from collect ,CommodityInfo\n" +
            " where collect.CommodityID=commodityinfo.ID and collect.CommodityID=#{id} \n" +
            " GROUP BY CommodityID")
    List<Map<String,Object>> selectByID(Integer id);
    /*
      查询价格
     */

    @Select("select Price from CommodityInfo where ID=#{id}")
    Float selectPrice(Integer id);
    /*
      查询所有种类是学习用品的商品的集合以及，收藏个数
        String 是商品名字
        object 是商品对象
     */



    /*
      查询发布的
     */

    @Select("select * from CommodityInfo where SellerID=#{id} and Status != '已下架'")
    List<CommodityInfo> selectPulished(Integer id);

    /*
      查询买到的
     */

    @Select("select *\n" +
            "  from `order` a\n" +
            "  where  a.UserID =#{id}\n" +
            "order by OrderStatus DESC ,SubmitTime ASC")
    List<Order> selectBought(Integer id);

    /*
      查询卖出的
     */

    @Select("select *\n" +
            "  from `order` a\n" +
            "  where  a.SellUserID =#{id}\n" +
            "order by OrderStatus DESC ,SubmitTime ASC")
    List<Order> selectSold(Integer id);

    /*
      查询收藏的
     */

    @Select("select CommodityInfo.* \n" +
            "from collect,CommodityInfo\n" +
            "where  collect.UserID = #{id} and commodityinfo.ID  = collect.CommodityID \n" +
            "GROUP BY commodityinfo.ID")
    List<CommodityInfo> selectLiked(Integer id);

    @Select("select count(*)  as 'liked'\n" +
            "    from collect,CommodityInfo\n" +
            "     where  collect.UserID = #{id} and commodityinfo.ID  = collect.CommodityID")
    Integer getLiked(Integer id);

    @Select("select count(*) as 'sold'\n"+
            "from `order`  a,CommodityInfo\n"+
            "where  a.SellUserID =#{id} and commodityinfo.ID  = a.CommodityID")
    Integer getSold(Integer id);

    @Select("select count(*) as 'bought'\n"+
            "from `order`  a,CommodityInfo\n"+
            "where  a.UserID =#{id} and commodityinfo.ID  = a.CommodityID"
    )
    Integer getBought(Integer id);

    @Select("select count(*) as 'pulished'from CommodityInfo where SellerID=#{id}")
    Integer getPulished(Integer id);

    @Select("SELECT count(*)\n" +
            "from commodityinfo\n" +
            "WHERE Category = #{category}")
    Float getCategoryNum(String category);

    @Select("SELECT count(*)\n" +
            "from commodityinfo\n" +
            "WHERE `Status` = #{status}")
    Integer getStatusNum(String status);

}