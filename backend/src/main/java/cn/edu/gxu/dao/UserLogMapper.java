package cn.edu.gxu.dao;

import cn.edu.gxu.model.UserLog;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface UserLogMapper extends BaseMapper<UserLog> {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLog record);

    int insertSelective(UserLog record);

    UserLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLog record);

    int updateByPrimaryKey(UserLog record);
    UserLog selectByUserId(Integer Id);
    int selectIdByAccount(String account);
    List<UserLog> hadMail(String mail);
}