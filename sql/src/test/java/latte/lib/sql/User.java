package latte.lib.sql;

import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "user_tbl")
@Getter
@Setter
public class User {
  @Column(name = "name", nullable = false, length = 50)
  String name;

  @Column(name = "age", nullable = false, type = "int")
  Integer age;
}
