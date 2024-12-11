package org.example.java2final.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnswerTimeDistributionVO {
    private Integer elapsedMinutes;
    private Integer totalAnswers;
    private Integer acceptedAnswers;
    private Double acceptanceRate;
}