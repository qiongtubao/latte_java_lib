package latte.lib.db.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
    Long id = null;
    String name;
    int age;
}
