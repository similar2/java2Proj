package org.example.java2final.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.java2final.config.Result;
import org.example.java2final.pojo.Activity;
import org.example.java2final.service.ActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;

@Tag(name = "活动记录接口")
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    /**
     * 获取所有活动记录（支持分页）
     *
     * @param pageSize    每页记录数，默认值为10
     * @param currentPage 当前页码，默认值为1
     * @return Result 包含分页后的活动记录列表
     */
    @Operation(summary = "获取所有活动记录（分页）")
    @GetMapping("/all")
    public Result<IPage<Activity>> getAllActivities(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int currentPage) {
        return activityService.getAllActivities(pageSize, currentPage);
    }

    @Operation(summary = "根据活动 ID 获取问题")
    @GetMapping("/question")
    public Result<?> getQuestionByActivityId(@RequestParam Long activityId) {
        return Result.success(activityService.getQuestionByActivityId(activityId));
    }
}
