package latte.lib.api.config;

import latte.lib.common.serialization.JsonUtils;

public class StringDynamicConfig extends AbstractDynamicConfig {
  public StringDynamicConfig(String result) throws Exception {
    cached = JsonUtils.decodeMap(result, String.class, Object.class);
  }

  public void update(String result) throws Exception {
    cached = JsonUtils.decodeMap(result, String.class, Object.class);
  }
}
