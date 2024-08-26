package latte.lib.sql.mysql;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MysqlClientInfo {
  String address;
  int port;
  String database;
  String username;
  String password;
  public MysqlClientInfo() {

  }

}
