package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.UserDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
@Service
public interface MailService {
    boolean registered(UserDto userVo, HttpSession session);
    boolean sendMimeMail( String email, HttpSession session);
    boolean hadMail(String email);
}
