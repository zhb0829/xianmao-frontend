package cn.edu.gxu.service.Imp;


import cn.edu.gxu.service.ImageToBase64;
import cn.edu.gxu.utils.Base64Utils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageToBase64Impl implements ImageToBase64 {
    @Override
    public String ImageToBase64(String imgPath) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        String dir = System.getProperty("user.dir");
        imgPath = dir+"\\"+imgPath;

        File  imageFile = new File(imgPath);

        if (!imageFile.exists()){
            return  null;
        }
        else
            return Base64Utils.getImageBase64Str(imgPath);
          /*  try {
                in = new FileInputStream(imgPath);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
        Base64Utils.GetImageStr();
            // 返回Base64编码过的字节数组字符串*/
    }

}
