package latte.lib.sql;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(name, user.name)
        && Objects.equals(age, user.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, age);
  }
}
