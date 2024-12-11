package org.example.java2final.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomScoreVO {
    private Integer accountAgeDays;
    private Double qualityMetric;
}
