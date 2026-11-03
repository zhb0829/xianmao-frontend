package cn.edu.gxu.service;

import org.springframework.stereotype.Service;

@Service
public interface ImageToBase64 {
    public String ImageToBase64(String imgPath);
}
