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
                WITH Stats AS (SELECT AVG(LENGTH(a.body))    AS avg_length,
                                      STDDEV(LENGTH(a.body)) AS stddev_length,
                                      AVG(q.view_count)      AS avg_view_count,
                                      STDDEV(q.view_count)   AS stddev_view_count
                               FROM answer a
                                        JOIN
                                    question q ON a.question_id = q.question_id),
                Normalized
                    AS (SELECT EXTRACT(DAY FROM CURRENT_DATE - TO_TIMESTAMP(u.creation_date))       AS account_age_days,
                                  LENGTH(a.body)                                                       AS answer_length,
                                  q.view_count,
                                  (LENGTH(a.body) - Stats.avg_length) / NULLIF(Stats.stddev_length, 0) AS normalized_length,
                                  (q.view_count - Stats.avg_view_count) /
                                  NULLIF(Stats.stddev_view_count, 0)                                   AS normalized_view_count
                         FROM "user" u
                                  JOIN
                              answer a ON CAST(u.user_id AS BIGINT) = CAST(a.owner_user_id AS BIGINT)
                                  JOIN
                              question q ON a.question_id = q.question_id,
                              Stats),
                WeightedQuality AS (SELECT account_age_days,
                                        answer_length,
                                        view_count,
                                        normalized_length,
                                        normalized_view_count,
                                        (0.33 * normalized_length + 0.67 * normalized_view_count) AS quality_metric -- Adjust weights here
                                   FROM Normalized)
                SELECT account_age_days,
                       quality_metric
                FROM WeightedQuality
                ORDER BY quality_metric DESC
                LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<CustomScoreVO> getCustomScore(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

}
