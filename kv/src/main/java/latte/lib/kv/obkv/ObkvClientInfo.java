package latte.lib.kv.obkv;

import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ObkvClientInfo {
    String database;
    String configServerAddr;
    String fullUserName;
    String password;
    String sysUserName;
    String sysPassword;
    Map<String, String> properties;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObkvClientInfo that = (ObkvClientInfo) o;
    return Objects.equals(database, that.database) && Objects.equals(
        configServerAddr, that.configServerAddr) && Objects.equals(fullUserName,
        that.fullUserName) && Objects.equals(password, that.password)
        && Objects.equals(sysUserName, that.sysUserName) && Objects.equals(
        sysPassword, that.sysPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(database, configServerAddr, fullUserName, password, sysUserName,
        sysPassword);
  }

  @Override
  public String toString() {
    return "ObTableClientInfo{" +
        "database='" + database + '\'' +
        ", configServerAddr='" + configServerAddr + '\'' +
        ", fullUserName='" + fullUserName + '\'' +
        ", password='" + password + '\'' +
        ", sysUserName='" + sysUserName + '\'' +
        ", sysPassword='" + sysPassword + '\'' +
        '}';
  }
}
