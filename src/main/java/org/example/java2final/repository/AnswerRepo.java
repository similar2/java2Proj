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

    public List<AnswerTimeDistributionVO> getAnswerTimeDistribution(int pageSize, int currentPage) {
        int offset = (currentPage - 1) * pageSize;
        return answerDAO.getAnswerTimeDistributions(pageSize, offset);
    }

    public List<ReputationScoreVO> getReputationScore(int pageSize, int currentPage) {
        int offset = (currentPage - 1) * pageSize;
        return answerDAO.getReputationScore(pageSize, offset);
    }

    public List<CustomScoreVO> getCustomScore(int pageSize, int currentPage) {
        int offset = (currentPage - 1) * pageSize;
        return answerDAO.getCustomScore(pageSize, offset);
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
