package org.example.java2final.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.java2final.config.Result;
import org.example.java2final.service.AnswerService;
import org.example.java2final.vo.AnswerTimeDistributionVO;
import org.example.java2final.vo.CustomScoreVO;
import org.example.java2final.vo.ReputationScoreVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "答案质量接口")
@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @Operation(summary = "答案创建时间与被接受率的折线图")
    @GetMapping("/time")
    public Result<List<AnswerTimeDistributionVO>> getLineChart(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int currentPage)  {
        return answerService.getAnswerTimeDistribution(pageSize, currentPage);
    }

    @Operation(summary = "答案得分与用户声誉关系")
    @GetMapping("/reputation")
    public Result<List<ReputationScoreVO>> getReputationScore(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int currentPage) {
        return answerService.getReputationScore(pageSize, currentPage);
    }

    @Operation(summary = "问题阅读量和答案长度与用户创建时长的关系")
    @GetMapping("/custom")
    public Result<List<CustomScoreVO>> getCustomScore(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int currentPage) {
        return answerService.getCustomScore(pageSize, currentPage);
    }

}
