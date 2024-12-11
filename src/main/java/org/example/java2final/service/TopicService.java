package org.example.java2final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java2final.config.Result;
import org.example.java2final.repository.QuestionRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScope
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService {
    private final QuestionRepo questionRepo;

    public Result<List<Map<String, Integer>>> getTopic(Long size) {
        if (size < 1 || size > 100) {
            return Result.error("The size must be between 1 and 100");
        }
        return Result.success(questionRepo.findTopic(size));
    }

    public Result<Map<String, Integer>> getTopic(String topic) {
        Map<String, Integer> map = new HashMap<>();
        if (topic == null || topic.isEmpty()) {
            return Result.error("Topic is empty");
        }
        Integer frequency = questionRepo.findTopic(topic);
        map.put(topic, frequency);
        return Result.success(map);
    }
}
