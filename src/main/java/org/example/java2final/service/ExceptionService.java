package org.example.java2final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java2final.config.Result;
import org.example.java2final.repository.ExceptionRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.Map;

@ApplicationScope
@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionService {
    private final ExceptionRepo exceptionRepo;

    public Result<List<Map<String, Integer>>> getTopNExceptions(int size) {
        // Validate input
        if (size <= 0) {
            return Result.error("Invalid size: must be greater than 0");
        }

        // Fetch data from repository
        List<Map<String, Integer>> exceptions = exceptionRepo.getExceptions(size);

        // Validate output
        if (exceptions == null || exceptions.isEmpty()) {
            return Result.error("No exceptions found for the requested size");
        }

        // Return success response
        return Result.success(exceptions);
    }

    public Result<Map<String, Integer>> getExceptionFrequency(String exceptionName) {
        // Validate input: Check if exceptionName is not null or empty
        if (exceptionName == null || exceptionName.trim().isEmpty()) {
            return Result.error("Invalid exception name: must not be null or empty");
        }
        exceptionName = exceptionName.trim().toLowerCase();

        // Validate input: Ensure exceptionName ends with a valid suffix using regex
        // The regex is case-insensitive due to the (?i) flag
        if (!exceptionName.matches("(?i)^[a-z0-9_]+(exception|error|throwable|failure)$")) {
            return Result.error("Invalid exception name: must end with 'Exception', 'Error', 'Throwable', or 'Failure'");
        }

        // Fetch data from repository
        // The repository must also handle exception names in a case-insensitive manner
        // This assumes that the repository's `getException` method has been adjusted accordingly
        Map<String, Integer> exceptionFrequency = exceptionRepo.getException(exceptionName);

        // Validate output: Check if data exists for the given exceptionName
        if (exceptionFrequency == null || exceptionFrequency.isEmpty()) {
            return Result.error("No data found for exception: " + exceptionName);
        }

        // Return success response
        return Result.success(exceptionFrequency);
    }
}
