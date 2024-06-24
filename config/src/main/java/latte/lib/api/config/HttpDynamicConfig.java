package latte.lib.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import latte.lib.common.net.Requester;
import latte.lib.common.net.http.HttpRequestInfo;
import latte.lib.common.net.http.HttpRequester;

import org.springframework.http.HttpMethod;


public class HttpDynamicConfig extends AbstractIntervalDynamicConfig {

  public String httpUrl = "";

  Requester requester;
  public HttpDynamicConfig(String httpUrl, int interval, ScheduledExecutorService scheduledExecutorService)
      throws Exception {
    super(interval, scheduledExecutorService);
    this.httpUrl = httpUrl;
    requester = new HttpRequester();
    this.loadCached();
  }

//


  @Override
  Map<String, Object> loadCached() throws Exception {
    return requester.requestMap(new HttpRequestInfo<>(
        this.httpUrl,
        HttpMethod.GET,
        null
    ), String.class, Object.class);
  }
}
