package org.example.java2final.repository;

import org.example.java2final.dao.UserDAO;
import org.example.java2final.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final UserDAO userDAO;

    public UserRepo(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public boolean insertUser(User user) {
        return userDAO.insert(user) != 0;
    }
}
