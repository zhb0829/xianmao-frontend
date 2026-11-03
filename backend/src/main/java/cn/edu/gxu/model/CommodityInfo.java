package cn.edu.gxu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *  apiModel注释说明此对象是对应那张表
 *  tableid表明主键
 *  apiModelProperty 表明类中属性对应表中字段
 *
 */
@ApiModel()
@Data
@TableName("CommodityInfo")
public class CommodityInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("商品名称")
    @TableField("TradeName")
    private String tradeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstPhoto() {
        return firstPhoto;
    }

    public void setFirstPhoto(String firstPhoto) {
        this.firstPhoto = firstPhoto;
    }

    @ApiModelProperty("商品价格")
    @TableField("price")
    private Float price;
    @ApiModelProperty("商品描述")
    @TableField("description")
    private String description;
    @ApiModelProperty("发布时间")
    @TableField("time")
    private Date time;
    @ApiModelProperty("卖家id")
    @TableField("sellerId")
    private Integer sellerId;
    @ApiModelProperty("发布地址")
    @TableField("shippingAddress")
    private String shippingAddress;
    @ApiModelProperty("分类类别")
    private String category;
    @ApiModelProperty("购买状态")
    private String status;
    @ApiModelProperty("封面图片")
    @TableField("firstPhoto")
    private String firstPhoto;

}