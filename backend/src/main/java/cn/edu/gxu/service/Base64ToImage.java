package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.DelImageInfo;
import cn.edu.gxu.controller.dto.ImageInfo;
import cn.edu.gxu.controller.dto.ImagesInfo;

import org.springframework.stereotype.Service;


@Service
public interface Base64ToImage {
    static  String saveImageFilePath ="project\\src\\main\\resources\\static\\";
    public String Base64Toimg2(ImageInfo imageInfo,int commodityId);
    public  String Base64Toimgs(ImagesInfo imagesInfo) ;
    String   Base64ToAvatar(String imageFilePath,String base64);
    public String  DelImage(DelImageInfo delImageInfo,String imageFilePath);

}
