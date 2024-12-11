package org.example.java2final.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.java2final.config.Result;
import org.example.java2final.service.TopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "常见话题接口")
@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @Operation(summary = "获取最频繁的N个topic")
    @GetMapping("/top")
    public Result<List<Map<String, Integer>>> top(
            @RequestParam Long size
    ) {
        return topicService.getTopic(size);
    }

    @Operation(summary = "获取特定topic的出现次数")
    @GetMapping("")
    public Result<Map<String, Integer>> getTopic(
            @RequestParam String topic
    ) {
        return topicService.getTopic(topic);
    }
}
