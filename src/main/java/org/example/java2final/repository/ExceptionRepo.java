package org.example.java2final.repository;

import org.example.java2final.dao.AnswerDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ExceptionRepo {
    private final AnswerDAO answerDAO;

    public ExceptionRepo(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }

    public List<Map<String, Object>> getExceptionsByType(Integer size, String type) {
        return answerDAO.findExceptionFrequenciesByType(size, type);
    }


    public Map<String, Integer> getException(String exceptionName) {
        Integer frequency = answerDAO.findSpecificExceptionFrequency(exceptionName);
        if (frequency == null) {
            frequency = 0;
        }
        return Map.of(exceptionName, frequency);
    }

}
