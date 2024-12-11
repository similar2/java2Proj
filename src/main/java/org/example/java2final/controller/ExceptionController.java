package org.example.java2final.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.java2final.config.Result;
import org.example.java2final.service.ExceptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "常见异常接口")
@RestController
@RequestMapping("/exception")
@RequiredArgsConstructor
public class ExceptionController {
    private final ExceptionService exceptionService;

    @Operation(summary = "获取top N 常见异常")
    @GetMapping("/top")
    public Result<List<Map<String, Integer>>> getTopN(
            @RequestParam int size
    ) {
        return exceptionService.getTopNExceptions(size);
    }

    @Operation(summary = "获取异常出现次数")
    @GetMapping("")
    public Result<Map<String, Integer>> getExceptionFrequency(
            @RequestParam String exceptionName
    ) {
        return exceptionService.getExceptionFrequency(exceptionName);
    }
}
