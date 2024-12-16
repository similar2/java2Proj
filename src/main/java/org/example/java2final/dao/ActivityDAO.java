package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.java2final.pojo.Activity;
import org.example.java2final.pojo.Question;

import java.util.List;

@Mapper
public interface ActivityDAO extends MPJBaseMapper<Activity> {
    default List<Activity> getAllActivities() {
        return this.selectList(null);
    }


    @Select("SELECT * FROM question WHERE question_id = #{questionId}")
    Question getQuestionByQuestionId(Long questionId);
}
