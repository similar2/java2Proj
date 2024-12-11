package org.example.java2final.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.java2final.dao.UserDAO;
import org.example.java2final.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final UserDAO userDAO;

    public UserRepo(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private boolean assertNoDuplicate(User user) {
        return userDAO.selectCount(new LambdaQueryWrapper<User>().eq(User::getUserId, user.getUserId())) > 0;
    }

    public boolean insertUser(User user) {
        if (assertNoDuplicate(user)) {
            return false;
        }
        return userDAO.insert(user) != 0;
    }
}
