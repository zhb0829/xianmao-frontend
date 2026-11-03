package cn.edu.gxu.controller.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PersonUpdateDto {

    private Integer id;
    private String address;
    private String nickname;
    private String telephone;
    private String avatar;
}
