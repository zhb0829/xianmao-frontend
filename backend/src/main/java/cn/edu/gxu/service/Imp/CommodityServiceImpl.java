package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.AllCommodityInfoDto;

import cn.edu.gxu.dao.CommodityInfoMapper;
import cn.edu.gxu.dao.PersonInfoMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.Collect;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Images;
import cn.edu.gxu.service.*;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class CommodityServiceImpl extends ServiceImpl<CommodityInfoMapper,CommodityInfo> implements CommodityService  {

    @Resource
    CollectService collectService;
    @Resource
    ImageToBase64 imageToBase64;
    @Resource
    Base64ToImage base64ToImage;
    @Resource
    CommodityInfoMapper  commodityInfoMapper;

    @Resource
    private PersonInfoMapper personInfoMapper;

    private  static int idLimit =9999;
    

    @Override
    public boolean deleteCommodity(int commodityID) {
        commodityInfoMapper.deleteByPrimaryKey(commodityID);
        return  true;
    }


    public int  getCommodityId() {
        int commodityId;
        long seed =     System.currentTimeMillis();
        Random r = new Random(seed);
        commodityId = r.nextInt(idLimit);
        CommodityInfo  commodityInfo= getById(commodityId);
        while (commodityInfo!=null){
            idLimit +=1000;
            commodityId =r.nextInt(idLimit);
        }
        return  commodityId;
    }


    static LinkedList<CommodityInfo> presentData = new LinkedList<CommodityInfo>(); ;


    @Resource
    ImageService imageService;
    @Override
    public LinkedList<CommodityInfo> queryMoHu(String tradeName) {
        Page<CommodityInfo> page = new Page<>();
        presentData  = commodityInfoMapper.selectSelective(tradeName);
        return  presentData;
    }

    @Override
    public LinkedList<CommodityInfo> queryCommodityByPrice(double lowPrice, double highPrice) {
        LinkedList<CommodityInfo> priceInfo = new LinkedList<>();
         Collections.copy(priceInfo,presentData);
        priceInfo.sort((o1, o2) -> o1.getPrice() - o2.getPrice() >0 ? 1 : -1);
        for (int i = 0; i < priceInfo.size(); i++) {
            if (priceInfo.get(i).getPrice()<lowPrice||(priceInfo.get(i).getPrice()>highPrice&&highPrice!=0.0)){
                priceInfo.remove(i);
            }
        }
        return  priceInfo;
    }
    @Override
    public LinkedList<CommodityInfo> queryCommodityByTime() {
        presentData.sort((o1, o2) -> o1.getPrice()-o2.getPrice()>0? 1:-1);
        return  presentData;
    }

    /*
       商品全查询
     */
    @Override
    public  Page<AllCommodityInfoDto> SelectAll(int currentPage,int pageSize,String category,String tradeName,String status,Integer timeFlag,Integer priceFlag) {
        Page <AllCommodityInfoDto> allCommodityInfoDtoPage =new Page<>();
        Page<CommodityInfo> page = new Page<>(currentPage,pageSize);
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        QueryWrapper <CommodityInfo  > queryWrapper = new QueryWrapper<>();
        if (timeFlag!=-1){
            if(timeFlag%2==0)
                queryWrapper.orderByAsc("time");
            else queryWrapper.orderByDesc("time");
        }
        if (priceFlag!=-1){
            if(priceFlag%2==0)
                queryWrapper.orderByAsc("Price");
            else queryWrapper.orderByDesc("price");
        }
        if (status!=""){
            queryWrapper.eq("status",status);
        }
         if (tradeName!=""){
            queryWrapper.like(true,"tradeName",tradeName);
        }if (category!=""){
            queryWrapper.eq("category",category);
        }
         queryWrapper.eq("status","已审核");
        List<CommodityInfo> allCommodity = commodityInfoMapper.selectPage(page,queryWrapper).getRecords();
        List<AllCommodityInfoDto> allCommodityInfoDtos = new LinkedList<>();

        for (int i = 0; i < allCommodity.size(); i++) {
            AllCommodityInfoDto allCommodityInfoDto = new AllCommodityInfoDto();
            BeanUtil.copyProperties(allCommodity.get(i),allCommodityInfoDto,true);
            List<Images> images =  imageService.getImageUrl(allCommodityInfoDto.getCommodityID());
            List<String> base64 = new LinkedList<>();
            //轮播图与这些合并
            for (int j = 0; j <images.size(); j++) {
                if (j>=7){
                    break;
                }
                base64.add(j,imageToBase64.ImageToBase64(images.get(j).getCommodityImage()));
            }
            allCommodityInfoDto.setSlideshow(base64);
            QueryWrapper<Collect > collectQueryWrapper = new QueryWrapper<>();
            collectQueryWrapper.eq("commodityID",allCommodity.get(i).getId());
            allCommodityInfoDto.setCollectCount(collectService.count(collectQueryWrapper));
            String fistPhoto =imageToBase64.ImageToBase64(allCommodityInfoDto.getFirstPhoto());
            allCommodityInfoDto.setCommodityID(allCommodity.get(i).getId());
            allCommodityInfoDto.setFirstPhoto(fistPhoto);
            allCommodityInfoDtos.add(i,allCommodityInfoDto);
        }
        BeanUtil.copyProperties(page,allCommodityInfoDtoPage,true);
        allCommodityInfoDtoPage.setRecords(allCommodityInfoDtos);
        allCommodityInfoDtoPage.setTotal(page.getTotal());
        return  allCommodityInfoDtoPage;
    }
    /*
        查询单个商品
     */
    @Override
    public List<Map<String,Object>> SelectByID(Integer id)
    {
        return commodityInfoMapper.selectByID(id);
    }

    /**
     *
     * @param GoodsID
     * @return 价格
     */
    @Override
    public Float selectPrice(Integer GoodsID)
    {
        return commodityInfoMapper.selectPrice(GoodsID);
    }

    @Override
    public boolean launchCommodityInfo(CommodityInfo commodityInfo) {
        try {
            commodityInfoMapper.insert(commodityInfo);
            return  true;
        }catch (Exception e){
            log.error(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"发布商品，失败请重新发布");
        }
    }

    /**
     *
     * @param CommodityID
     * @param UserID
     * @return
     */



}
