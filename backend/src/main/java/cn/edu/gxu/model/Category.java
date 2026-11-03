package cn.edu.gxu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *  apiModel注释说明此对象是对应那张表
 *  tableid表明主键
 *  apiModelProperty 表明类中属性对应表中字段
 *
 */
@ApiModel("Category对象")
public class Category {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("类别")
    private String category;

    public Category(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", category='" + category + '\'' +
                '}';
    }
}