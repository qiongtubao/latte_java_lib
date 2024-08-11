package latte.lib.sql;

import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Table;
import latte.lib.api.db.sql.annotation.Type;
import lombok.Getter;
import lombok.Setter;

@Table(name = "user_tbl")
@Getter
@Setter
public class User {
  @Id(name = "id")
  Integer id;

//  @Column(name = "name", nullable = false, length = 50)
  String name;

//  @Column(name = "age", nullable = false, type = Type.INTEGER)
  Integer age;

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", age=" + age +
        '}';
  }
}
