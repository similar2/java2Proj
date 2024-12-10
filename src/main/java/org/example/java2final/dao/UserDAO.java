package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.java2final.pojo.User;

@Mapper
public interface UserDAO extends MPJBaseMapper<User> {
}