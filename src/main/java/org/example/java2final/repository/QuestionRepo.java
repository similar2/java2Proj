package org.example.java2final.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.java2final.dao.QuestionDAO;
import org.example.java2final.pojo.Question;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QuestionRepo {
    private final QuestionDAO questionDAO;

    public QuestionRepo(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public Integer findTopic(String topic) {
        return questionDAO.findTopicFrequency(topic);
    }

    public List<Map<String, Integer>> findTopic(Long size) {
        return questionDAO.findTopTags(size);
    }

    private boolean assertNoDuplicate(Question question) {
        return questionDAO.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionId, question.getQuestionId())) > 0;
    }

    public boolean insert(Question question) {
        if (assertNoDuplicate(question)) {
            return false;
        }
        return questionDAO.insert(question) != 0;
    }
}