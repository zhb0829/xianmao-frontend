package cn.edu.gxu.service.Imp;

import cn.edu.gxu.dao.ImagesMapper;
import cn.edu.gxu.model.Images;
import cn.edu.gxu.service.ImageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class ImageServiceImpl extends ServiceImpl<ImagesMapper, Images> implements ImageService  {
    @Override
    public List<Images> getImageUrl(int commodityId) {
        QueryWrapper<Images> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commodityId",commodityId);
        return list(queryWrapper) ;
    }

    @Override
    public boolean saveImageUrl(int commodityID, String imageUrl) {
        Images images = new Images();
        images.setCommodityid(commodityID);
        images.setCommodityImage(imageUrl);
        return  save(images);
    }

}
