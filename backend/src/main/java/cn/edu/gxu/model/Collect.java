package cn.edu.gxu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 *  apiModel注释说明此对象是对应那张表
 *  tableid表明主键
 *  apiModelProperty 表明类中属性对应表中字段
 *
 */
@ApiModel("收藏对象")
public class Collect {
    public Collect(Integer userId, Integer commodityId) {
        this.userId = userId;
        this.commodityId = commodityId;
    }

    public Collect() {
    }

    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;


    @ApiModelProperty()
    private Integer userId;
    private Integer commodityId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


}


