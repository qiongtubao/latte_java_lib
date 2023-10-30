package latte.lib.db.example.impls;

import latte.lib.db.example.User;

import java.util.List;

public interface UserMapper {
    void insertEntity(User user);

    List<User> getAllEntities();
    void createUserTable();
    
}
