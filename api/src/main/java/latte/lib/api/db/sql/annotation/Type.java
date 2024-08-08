package latte.lib.api.db.sql.annotation;

import java.sql.Time;

public enum Type {
  // 字符串类型
  VARCHAR,  //变长字符类型
  CHAR,     //定长字符类型

  // 整数类型
  INTEGER,  //整数类型
  FLOAT,    //浮点和定点类型

  //日期和时间类型
  DATE;      //日期类型

  public static Type get(Class glazz) {
    if (glazz.equals(Integer.class)
    || glazz.equals(int.class)
    || glazz.equals(Long.class)
    || glazz.equals(long.class)) {
      return INTEGER;
    }  if (glazz.equals(Float.class)
    || glazz.equals(Double.class)) {
      return FLOAT;
    } else if (glazz.equals(String.class)) {
      return VARCHAR;
    } else if (glazz.equals(Time.class)) {
      return DATE;
    }

    return null;
  }
}
