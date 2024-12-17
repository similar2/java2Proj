package org.example.java2final.dao;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.java2final.pojo.Answer;

import org.example.java2final.vo.AnswerTimeDistributionVO;
import org.example.java2final.vo.CustomScoreVO;
import org.example.java2final.vo.ReputationScoreVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnswerDAO extends MPJBaseMapper<Answer> {
    @Select("""
                SELECT exception_name AS name,
                       COUNT(*) AS frequency,
                       CASE
                           -- Match 'Exception' or 'Throwable' but exclude 'Error' types more specifically
                           WHEN exception_name LIKE '%Exception%' AND exception_name NOT LIKE '%Error%' THEN 'exception'
                           WHEN exception_name LIKE '%Error%' OR exception_name LIKE '%Failure%' THEN 'error'
                           ELSE 'unknown'
                       END AS type
                FROM (
                    -- Extract exceptions from the question table
                    SELECT unnest(regexp_matches(content, '([a-zA-Z0-9_]+(Exception|Error|Throwable|Failure))', 'g')) AS exception_name
                    FROM question
                    UNION ALL
                    -- Extract exceptions from the answer table
                    SELECT unnest(regexp_matches(body, '([a-zA-Z0-9_]+(Exception|Error|Throwable|Failure))', 'g')) AS exception_name
                    FROM answer
                ) AS exceptions
                WHERE exception_name NOT IN ('Exception', 'Error')  -- Exclude raw terms
                AND (
                    ('exception' = #{type} AND exception_name LIKE '%Exception%' AND exception_name NOT LIKE '%Error%') OR
                    ('error' = #{type} AND exception_name LIKE '%Error%')
                )
                GROUP BY exception_name
                ORDER BY frequency DESC
                LIMIT #{size}
            """)
    List<Map<String, Object>> findExceptionFrequenciesByType(@Param("size") Integer size, @Param("type") String type);


    @Select("""
                SELECT
                       COUNT(*) AS frequency
                FROM (
                         -- Extract exceptions from the question table
                         SELECT unnest(regexp_matches(content, '([a-zA-Z0-9_]+(Exception|Error|Throwable|Failure))', 'gi')) AS exception_name
                         FROM question

                         UNION ALL

                         -- Extract exceptions from the answer table
                         SELECT unnest(regexp_matches(body, '([a-zA-Z0-9_]+(Exception|Error|Throwable|Failure))', 'gi')) AS exception_name
                         FROM answer
                     ) AS exceptions
                WHERE LOWER(exception_name) = LOWER(#{exceptionName}) -- Filter by specific exception using case-insensitive comparison
                GROUP BY LOWER(exception_name)
            """)
    Integer findSpecificExceptionFrequency(@Param("exceptionName") String exceptionName);

    @Select("""
            SELECT FLOOR((EXTRACT(EPOCH FROM (TO_TIMESTAMP(answer.creation_date) - TO_TIMESTAMP(question.creation_date))) / 60)) AS elapsed_minutes,
                   COUNT(*) AS total_answers,
                   SUM(CASE WHEN answer.is_accepted THEN 1 ELSE 0 END) AS accepted_answers,
                   SUM(CASE WHEN answer.is_accepted THEN 1 ELSE 0 END) * 1.0 / COUNT(*) AS acceptance_rate
            FROM question
            JOIN answer ON question.question_id = answer.question_id
            GROUP BY elapsed_minutes
            ORDER BY elapsed_minutes
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<AnswerTimeDistributionVO> getAnswerTimeDistributions(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    @Select("""
                SELECT u.reputation AS reputation_score,
                       AVG(a.score) AS average_score
                FROM "user" u
                         JOIN
                     answer a ON CAST(u.user_id AS bigint) = CAST(a.owner_user_id AS bigint)
                GROUP BY u.reputation
                having AVG(a.score)<>0
                ORDER BY u.reputation
                LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<ReputationScoreVO> getReputationScore(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);


    @Select("""
            SELECT\s
                 EXTRACT(DAY FROM CURRENT_TIMESTAMP - TO_TIMESTAMP(u.creation_date)) AS account_age_days,
                 AVG(a.score) AS quality_metric
             FROM\s
                 "user" u
             JOIN\s
                 answer a\s
             ON\s
                    u.user_id = CAST(a.owner_user_id AS BIGINT)
             GROUP BY\s
                 account_age_days
             having AVG(a.score)<>0
             ORDER BY\s
                 account_age_days DESC
              LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<CustomScoreVO> getCustomScore(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

}
