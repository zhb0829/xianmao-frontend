package cn.edu.gxu.controller.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AllCommodityInfoDto {



    private int commodityID  ;
    @ApiModelProperty("商品名称")
    @TableField("tradeName")
    private String tradeName;
    @ApiModelProperty("商品价格")
    private Float price;
    @ApiModelProperty("分类类别")
    private String category;
    @ApiModelProperty("封面图片")
    private String firstPhoto;
    @ApiModelProperty("轮播图")
    List <String> slideshow;
    @ApiModelProperty("收藏数")
    long  collectCount;
}
