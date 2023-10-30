package latte.lib.db.example.impls;

import latte.lib.db.example.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_tbl (name, age) VALUES (#{name}, #{age})")
    void insertEntity(User user);

    @Select("SELECT * FROM user_tbl")
    List<User> getAllEntities();

    @Insert("Create table `user_tbl` (id int primary key AUTO_INCREMENT, name varchar(50), age int) ENGINE=InnoDB DEFAULT CHARSET=utf8;")
    void createUserTable();
    
}
