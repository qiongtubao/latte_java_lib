package latte.lib.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import latte.lib.common.net.Requester;
import latte.lib.common.net.http.HttpRequestInfo;
import latte.lib.common.net.http.HttpRequester;
import org.apache.http.HttpMessage;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

public class HttpDynamicConfig extends AbstractIntervalDynamicConfig {

  public String httpUrl = "";

  Requester requester;
  public HttpDynamicConfig(String httpUrl, int interval, ScheduledExecutorService scheduledExecutorService) {
    super(interval, scheduledExecutorService);
    this.httpUrl = httpUrl;
    requester = new HttpRequester();
  }

//


  @Override
  Map<String, String> loadCached() throws Exception {
    return requester.requestMap(new HttpRequestInfo<>(
        this.httpUrl,
        HttpMethod.GET,
        null
    ), String.class, String.class);
  }
}
