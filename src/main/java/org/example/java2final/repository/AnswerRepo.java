package org.example.java2final.repository;

import org.example.java2final.dao.AnswerDAO;
import org.example.java2final.pojo.Answer;
import org.springframework.stereotype.Repository;

@Repository
public class AnswerRepo {
    private final AnswerDAO answerDAO;

    public AnswerRepo(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }


    public boolean insert(Answer answer) {
        return answerDAO.insert(answer) != 0;
    }
}
