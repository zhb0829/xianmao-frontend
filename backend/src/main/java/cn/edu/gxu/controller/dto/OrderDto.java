package cn.edu.gxu.controller.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;

public class OrderDto {
    @ApiModelProperty("订单id,用随机数生成")
    int id;
    @ApiModelProperty("商品名称")
    @TableField("tradeName")
    private String tradeName;

    @ApiModelProperty("封面图片")
    private String firstPhoto;
    @ApiModelProperty("商品价格")
    private Float price;
    @ApiModelProperty("卖家名字")
    String sellerName ;
    @ApiModelProperty("买家名字")
    String userName;
    @ApiModelProperty("地址")
    String  address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getFirstPhoto() {
        return firstPhoto;
    }

    public void setFirstPhoto(String firstPhoto) {
        this.firstPhoto = firstPhoto;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
