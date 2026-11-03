package cn.edu.gxu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  apiModel注释说明此对象是对应那张表
 *  tableid表明主键
 *  apiModelProperty 表明类中属性对应表中字段
 *
 */
@ApiModel()
@Data
@TableName("PersonInfo")
public class PersonInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("用户id")
    private Integer userid;
    @ApiModelProperty("用户电话号码")
    private String telephone;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("收货地址")
    private String address;

    public PersonInfo() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}