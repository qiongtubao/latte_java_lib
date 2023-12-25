package latte.lib.db.example;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    Long id = null;
    String name;
    int age;
}
