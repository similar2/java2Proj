package org.example.java2final.repository;

import org.example.java2final.dao.QuestionDAO;
import org.example.java2final.pojo.Question;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepo {
    private final QuestionDAO questionDAO;

    public QuestionRepo(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }


    public boolean insert(Question question) {
        return questionDAO.insert(question) != 0;
    }
}