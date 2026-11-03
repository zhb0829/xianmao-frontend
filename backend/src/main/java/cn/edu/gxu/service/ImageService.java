package cn.edu.gxu.service;

import cn.edu.gxu.model.Images;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageService extends IService<Images> {
      List<Images> getImageUrl(int commodityId);

      boolean saveImageUrl(int commodityID, String imageUrl);
}

