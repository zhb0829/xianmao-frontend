package cn.edu.gxu.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImagesInfo {
    @ApiModelProperty("图片id")
    int  CommodityId;
    String base64str;
    @ApiModelProperty("")
    String uname;

}
