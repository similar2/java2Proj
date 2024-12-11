package org.example.java2final.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.java2final.dao.AnswerDAO;
import org.example.java2final.pojo.Answer;
import org.example.java2final.vo.AnswerTimeDistributionVO;
import org.example.java2final.vo.CustomScoreVO;
import org.example.java2final.vo.ReputationScoreVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnswerRepo {
    private final AnswerDAO answerDAO;

    public AnswerRepo(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }

    public List<AnswerTimeDistributionVO> getAnswerTimeDistribution() {
        return answerDAO.getAnswerTimeDistributions();
    }

    public List<ReputationScoreVO> getReputationScore() {
        return answerDAO.getReputationScore();
    }

    public List<CustomScoreVO> getCustomScore() {
        return answerDAO.getCustomScore();
    }

    private boolean assertNoDuplicate(Answer answer) {
        return answerDAO.selectCount(new LambdaQueryWrapper<Answer>().eq(Answer::getAnswerId, answer.getAnswerId())) > 0;
    }

    public boolean insert(Answer answer) {
        if (assertNoDuplicate(answer)) {
            return false;
        }
        return answerDAO.insert(answer) != 0;
    }
}
