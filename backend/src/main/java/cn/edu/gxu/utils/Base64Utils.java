package cn.edu.gxu.utils;


import org.apache.commons.codec.binary.Base64;


import java.io.*;

public class Base64Utils {
    // 对字节数组字符串进行Base64解码并生成图片
    //imgFilePath 待保存的本地路径
    public static boolean GenerateImage(String base64Str, String imgFilePath) {
        if (base64Str == null) // 图像数据为空
            return false;
        try {
            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64Str);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            //====
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 图片转化成base64字符串
    public static String getImageBase64Str(String imgSrcPath){
        InputStream in = null;
        byte[] data = null;
        byte[] encodeBase64 = null;
        String imgStr = "";
        //读取图片字节数组
        try {
            in = new FileInputStream(imgSrcPath);
            data = new byte[in.available()];
            in.read(data);
            encodeBase64 = Base64.encodeBase64(data);
            imgStr = new String(encodeBase64);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            data = null;
            encodeBase64 = null;
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        //返回Base64编码过的字节数组字符串
        return imgStr;
    }
}
