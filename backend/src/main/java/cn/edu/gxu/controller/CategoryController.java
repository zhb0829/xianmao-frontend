package cn.edu.gxu.controller;

import cn.edu.gxu.service.PersonInfoService;
import org.springframework.Web.bind.annotation.GetMapping;
import org.springframework.Web.bind.annotation.RequestMapping;
import org.springframework.Web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")

public class CategoryController {
    @Resource
    PersonInfoService personInfoService;

    @GetMapping("/getAllCategories")
    public List<Map<String,Object>> selectAllCategories()
    {
        return personInfoService.getAllCategory();
    }
}
