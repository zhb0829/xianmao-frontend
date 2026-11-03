package cn.edu.gxu.controller.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("用来返回commodity复合数据,包括轮播图")
@Data
public class CommodityDto {
    private int commodityID  ;
    @ApiModelProperty("商品名称")
    @TableField("tradeName")
    private String tradeName;
    @ApiModelProperty("商品价格")
    private Float price;
    @ApiModelProperty("商品描述")
    private String description;
    @ApiModelProperty("卖家id")
    @TableField("sellerId")
    private Integer sellerId;
    @ApiModelProperty("发布地址")
    @TableField("shippingAddress")
    private String shippingAddress;
    @ApiModelProperty("分类类别")
    private String category;
    @ApiModelProperty("封面图片")
    private String firstPhoto;

}
