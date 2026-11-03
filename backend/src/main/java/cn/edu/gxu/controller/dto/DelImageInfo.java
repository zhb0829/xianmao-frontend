package cn.edu.gxu.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DelImageInfo {
    @ApiModelProperty("商品id")
    int  CommodityId;
    String uname;
}
