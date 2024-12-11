package org.example.java2final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java2final.config.Result;
import org.example.java2final.repository.AnswerRepo;
import org.example.java2final.vo.AnswerTimeDistributionVO;
import org.example.java2final.vo.CustomScoreVO;
import org.example.java2final.vo.ReputationScoreVO;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@ApplicationScope
@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepo answerRepo;

    public Result<List<AnswerTimeDistributionVO>> getAnswerTimeDistribution() {
        List<AnswerTimeDistributionVO> answerTimeDistributionVOList = answerRepo.getAnswerTimeDistribution();
        return Result.success(answerTimeDistributionVOList);
    }

    public Result<List<ReputationScoreVO>> getReputationScore() {
        List<ReputationScoreVO> reputationScoreVOList = answerRepo.getReputationScore();
        return Result.success(reputationScoreVOList);
    }

    public Result<List<CustomScoreVO>> getCustomScore() {
        List<CustomScoreVO> customScoreVOList = answerRepo.getCustomScore();
        return Result.success(customScoreVOList);
    }
}
