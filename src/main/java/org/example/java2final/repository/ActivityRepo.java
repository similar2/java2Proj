package org.example.java2final.repository;

import org.example.java2final.dao.ActivityDAO;
import org.example.java2final.pojo.Activity;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityRepo {
    private final ActivityDAO activityDAO;

    public ActivityRepo(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public boolean addActivity(Activity activity) {
        return activityDAO.insert(activity) != 0;
    }
}
