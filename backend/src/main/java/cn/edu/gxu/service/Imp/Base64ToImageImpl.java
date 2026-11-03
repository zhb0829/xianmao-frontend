package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.DelImageInfo;
import cn.edu.gxu.controller.dto.ImageInfo;
import cn.edu.gxu.controller.dto.ImagesInfo;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.service.Base64ToImage;
import cn.edu.gxu.service.ImageToBase64;
import cn.edu.gxu.utils.Base64Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Transactional
@Service
public class Base64ToImageImpl implements Base64ToImage {
    /**
     * 这个方法存储封面图
     *
     * @param imageInfo
     * @return
     * @throws IOException
     */
    @Resource
    ImageToBase64 imageToBase64;
    @Override
    public String Base64Toimg2(ImageInfo imageInfo,int commodityId) {
        /*String imagename= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));*/
        String imaFilePath = saveImageFilePath +  commodityId+ "\\FirstPhoto" + "\\FirstPhoto.jpg";
        createFile(new File(imaFilePath));
        String base64str = imageInfo.getBase64str().split(",")[1];
        Boolean bl = Base64Utils.GenerateImage(base64str, imaFilePath);
        if (bl) {
            return  imaFilePath;
        } else
            throw  new ServiceException(Constants.CODE_500,"保存图片失败");
    }

    @Override
    public String Base64ToAvatar(String imaFilePath,String base64) {
        createFile(new File(imaFilePath));
        String base64str = base64.split(",")[1];
        Boolean bl = Base64Utils.GenerateImage(base64str, imaFilePath);
        if (bl) {
            return imaFilePath;
        }else throw  new ServiceException(Constants.CODE_500,"保存图片失败");
    }

    /**
     * 这个方法存储轮播图
     *
     * @param imagesInfo
     * @throws IOException
     */
    @Override
    public String Base64Toimgs(ImagesInfo imagesInfo)  {
        String imaFilePath = saveImageFilePath +  imagesInfo.getCommodityId() + "\\Photos" + "\\" + imagesInfo.getUname() + ".jpg";
        createFile(new File(imaFilePath));
        String base64str = imagesInfo.getBase64str().split(",")[1];
        Boolean bl = Base64Utils.GenerateImage(base64str, imaFilePath);
        if (bl) {
            return imaFilePath;
        }else throw  new ServiceException(Constants.CODE_500,"保存图片失败");

    }

    /**
     * 这个方法删除图片,同时返回图片的base64
     *
     * @param delImageInfo
     * @return
     * @throws IOException
     */
    @Override
    public String DelImage(DelImageInfo delImageInfo,String imaFilePath )  {
        String base64str = imageToBase64.ImageToBase64(imaFilePath);
        File file = new File(imaFilePath);
        try {
            file.delete();
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"删除图片失败");
        }

        return "data:image/jpeg;base64," + base64str;
    }

    public static void createFile(File file) {
        if (file.exists()) {
        } else {
            //getParentFile() 获取上级目录(包含文件名时无法直接创建目录的)
            if (!file.getParentFile().exists()) {
                //创建上级目录
                file.getParentFile().mkdirs();
            }
            try {
                //在上级目录里创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
