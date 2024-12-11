package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.java2final.pojo.Question;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionDAO extends MPJBaseMapper<Question> {

    @Select("""
                SELECT COUNT(*) AS frequency
                FROM (
                    SELECT jsonb_array_elements_text(tags::jsonb) AS tag
                    FROM question
                ) AS tag_list
                WHERE tag LIKE  #{topic}
            """)
    Integer findTopicFrequency(@Param("topic") String topic);

    @Select("""
                SELECT tag, COUNT(*) AS frequency
                FROM (
                    SELECT jsonb_array_elements_text(tags::jsonb) AS tag
                    FROM question
                ) AS tag_list
                WHERE tag <> 'java' -- Exclude "java"
                GROUP BY tag
                ORDER BY frequency DESC
                LIMIT #{size}
            """)
    List<Map<String, Integer>> findTopTags(@Param("size") Long size);
}
