package org.example.java2final.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java2final.config.Result;
import org.example.java2final.dao.ActivityDAO;
import org.example.java2final.dao.QuestionDAO;
import org.example.java2final.pojo.Activity; // 确保你的实体类路径正确
import org.example.java2final.pojo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScope
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityDAO activityDAO;

    /**
     * 获取所有活动记录（支持分页）
     *
     * @param pageSize    每页记录数
     * @param currentPage 当前页码
     * @return Result 包含分页后的活动记录列表
     */
    public Result<IPage<Activity>> getAllActivities(int pageSize, int currentPage) {
        // 创建分页对象
        Page<Activity> page = new Page<>(currentPage, pageSize);
        // 执行分页查询
        IPage<Activity> paginatedActivities = activityDAO.selectPage(page, null);
        return Result.success(paginatedActivities);
    }


    @Autowired
    private QuestionDAO questionDAO;

    public Question getQuestionByActivityId(Long activityId) {
        return questionDAO.getQuestionByQuestionId(activityId);
    }

    public Result<List<Map<String, Object>>> getTopTagsByReputation(Long minReputation, int limit) {
        List<Map<String, Object>> tags = activityDAO.getTopTagsByReputation(minReputation, limit);

        return Result.success(tags);
    }



}
