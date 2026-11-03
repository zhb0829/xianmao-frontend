package cn.edu.gxu.dao;

import cn.edu.gxu.model.PersonInfo;
import cn.edu.gxu.model.UserInfo;
import cn.edu.gxu.model.UserLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByUserId( int userId) ;

    String getName(Integer id);
}