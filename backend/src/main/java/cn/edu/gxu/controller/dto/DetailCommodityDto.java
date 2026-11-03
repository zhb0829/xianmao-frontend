package cn.edu.gxu.controller.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data

public class DetailCommodityDto {

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
    @ApiModelProperty("轮播图")
    List<String> slideShow;
    @ApiModelProperty("收藏数")
    long  collectCount;


}
