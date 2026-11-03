package cn.edu.gxu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import jdk.jfr.DataAmount;
import lombok.Data;

@ApiModel()
@TableName("Images")
@Data
public class Images  {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField("commodityImage")
    private String commodityImage;

    private Integer commodityid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommodityImage() {
        return commodityImage;
    }

    public void setCommodityImage(String commodityImage) {
        this.commodityImage = commodityImage == null ? null : commodityImage.trim();
    }


}