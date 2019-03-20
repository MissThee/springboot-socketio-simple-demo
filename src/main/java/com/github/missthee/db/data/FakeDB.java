package com.github.missthee.db.data;

import com.github.missthee.db.model.User;

import java.util.ArrayList;
import java.util.List;

public class FakeDB {
    public static List<User> userList = new ArrayList<User>() {{
        add(new User(1, "用户1"));
        add(new User(2, "用户2"));
        add(new User(3, "用户3"));
        add(new User(4, "用户4"));
        add(new User(5, "用户5"));
        add(new User(6, "用户6"));
        add(new User(7, "用户7"));
        add(new User(8, "用户8"));
        add(new User(9, "用户9"));
        add(new User(0, "用户0"));
    }};

    public static User selectUserById(Integer id) {
        for (User user : userList) {
            if (id.equals(user.getId())) {
                return user;
            }
        }
        return null;
    }
}
