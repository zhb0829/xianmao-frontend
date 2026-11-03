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
@ApiModel("订单对象")
@Data
@TableName("Order")
public class Order {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty()
    private Integer commodityId;

    private Integer userid;

    @TableField("sellUserId")
    private Integer sellUserId;

    private Integer personId;

    private Date submitTime;

    private Float submitPrices;

    private String logisticsStatus;

    private String orderStatus;
    public  Integer getCommodityId(){
        return  this.commodityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getSellUserId() {
        return sellUserId;
    }

    public void setSellUserId(Integer sellUserId) {
        this.sellUserId = sellUserId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Float getSubmitPrices() {
        return submitPrices;
    }

    public void setSubmitPrices(Float submitPrices) {
        this.submitPrices = submitPrices;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}