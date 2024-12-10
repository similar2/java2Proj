package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.java2final.pojo.Question;

@Mapper
public interface QuestionDAO extends MPJBaseMapper<Question> {
}
