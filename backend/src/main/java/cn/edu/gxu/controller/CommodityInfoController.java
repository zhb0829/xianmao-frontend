package cn.edu.gxu.controller;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.*;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Images;
import cn.edu.gxu.service.*;
import cn.edu.gxu.utils.TokenUtils;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.Web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/commodity")
public class CommodityInfoController {
    @Resource
    UserLogService userLogService;
    @Resource
    Base64ToImage base64ToImage;
    @Resource
    CommodityService commodityService;
    @Resource
    ImageService imageService;
    @Resource
    CollectService   collectService;

    @Resource
    ImageToBase64  imageToBase64;
    @GetMapping("/getCommodity")
    public Page<AllCommodityInfoDto> getAllCommodity(int currentPage, int pageSize, String category,String tradeName,String status,Integer timeFlag,Integer priceFlag){
        return  commodityService.SelectAll(currentPage,pageSize,category,tradeName, status,timeFlag,priceFlag);
    }
//
//    @GetMapping("/queryCommodityByName")
//    LinkedList<CommodityInfo> queryCommodityByName(String tradeName){
//        if (tradeName.length()==0){
//             throw  new ServiceException(Constants.CODE_400,"搜索条件不能为空");
//        }
//        return commodityService.queryMoHu(tradeName);
//    }
//    @GetMapping("/queryCommodityByPrice")
//    public LinkedList<CommodityInfo> queryCommodityByPrice (double lowPrice,double highPrice){
//     return    commodityService.queryCommodityByPrice(lowPrice,highPrice);
//    }
//    @GetMapping("/queryCommodityByTime")
//    public LinkedList<CommodityInfo> queryCommodityByTime(){
//        LinkedList<CommodityInfo> commodityInfos = commodityService.queryCommodityByTime();
//        System.out.println(commodityInfos.toString());
//        return commodityInfos;
//    }

    @PostMapping("/launchCommodityInfo")
    public  Map<String,Object>  launchCommodityInfo(@RequestBody CommodityDto commodityDto){
        int id = TokenUtils.getCurrentUser().getId();
        commodityDto.setSellerId(id);
        String firstPhoto =commodityDto.getFirstPhoto();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setUname(userLogService.getAccountById(id).getAccount());
        imageInfo.setBase64str(firstPhoto);
        int commodityId =commodityService.getCommodityId();
        String imagePath =  base64ToImage.Base64Toimg2(imageInfo,commodityId);
        CommodityInfo commodityInfo =new CommodityInfo();
        BeanUtil.copyProperties(commodityDto,commodityInfo,true);
        commodityInfo.setTime(new Date());
        commodityInfo.setFirstPhoto(imagePath);
        commodityInfo.setStatus("未审核");
        commodityInfo.setId(commodityId);
        commodityService.launchCommodityInfo(commodityInfo);
        Map<String,Object> map= new HashMap<>();
        map.put("commodityId",commodityId);
        return  map ;
    }

    /**
     *
     * @param
     * @param
     * @return
     */
    @PostMapping("/saveSlideShow")
    public  boolean  savaSlideShow(@RequestBody SlideShow slideShow, @RequestParam int commodityId){
        int id =TokenUtils.getCurrentUser().getId();
        String base64 [] = slideShow.getBase64();
        ImagesInfo imagesInfo = new ImagesInfo();
        imagesInfo.setCommodityId(commodityId);
        imagesInfo.setUname(userLogService.getAccountById(id).getAccount());
        for (int i = 0; i < base64.length; i++) {
            imagesInfo.setUname(Integer.toString(i));
            imagesInfo.setBase64str(base64[i]);
           String imagePath= base64ToImage.Base64Toimgs(imagesInfo);
           imageService.saveImageUrl(commodityId,imagePath);
        }
        return  true;
    }

    /**
     *
     * @param commodityID 为商品id
     * @param id 为卖家id;
     * @return
     */
    @GetMapping("/undercarriage")
    public  List<String> undercarriage(int commodityID,int id){

        List<Images>  list = imageService.getImageUrl(commodityID);
        List<String> base64 = new LinkedList<>();
        DelImageInfo delImageInfo = new DelImageInfo();
        for (int i = 0; i < list.size(); i++) {
            delImageInfo.setCommodityId(commodityID);
            delImageInfo.setUname(userLogService.getAccountById(id).getAccount());
            base64.add(base64ToImage.DelImage(delImageInfo,list.get(i).getCommodityImage()));
        }
        if (!commodityService.deleteCommodity(commodityID)){
            throw  new ServiceException(Constants.CODE_500,"删除失败");
        }
        return  base64;
    }
    @PostMapping("/queryCommodity")
    public  DetailCommodityDto  queryCommodity(int commodityId){
        CommodityInfo commodityInfo  = commodityService.getById(commodityId);
        List<Images>  images = imageService.getImageUrl(commodityId);
        List<String> slideShow = new LinkedList<>();
        for (int i = 0; i < images.size(); i++) {
            if (i>5){
                break;
            }
             slideShow.add(imageToBase64.ImageToBase64(images.get(i).getCommodityImage()));
        }
        DetailCommodityDto detailCommodityDto = new DetailCommodityDto();
        BeanUtil.copyProperties(commodityInfo,detailCommodityDto,true );
        detailCommodityDto.setSlideShow(slideShow);
        detailCommodityDto.setCollectCount(collectService.countCollect(commodityId));
        return  detailCommodityDto;
    }
}
