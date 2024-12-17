package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.java2final.pojo.Activity;
import org.example.java2final.pojo.Question;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityDAO extends MPJBaseMapper<Activity> {
    default List<Activity> getAllActivities() {
        return this.selectList(null);
    }


    @Select("SELECT * FROM question WHERE question_id = #{questionId}")
    Question getQuestionByQuestionId(Long questionId);


    @Select("SELECT * FROM activity WHERE user_reputation > #{reputationThreshold}")
    List<Activity> getActivitiesWithReputationGreaterThan(@Param("reputationThreshold") long reputationThreshold);

    @Select("""
            SELECT tag, COUNT(*) AS count
            FROM (
                SELECT UNNEST(STRING_TO_ARRAY(q.tags, ',')) AS tag
                FROM activity a
                JOIN question q ON a.question_id = q.question_id
                WHERE a.user_reputation > #{minReputation}
            ) AS unnested_tags
            WHERE tag not like '__java_' and tag not like '__java__'
            GROUP BY tag
            ORDER BY count DESC
            LIMIT #{limit};
            """)
    List<Map<String, Object>> getTopTagsByReputation(
            @Param("minReputation") Long minReputation,
            @Param("limit") int limit);
}


