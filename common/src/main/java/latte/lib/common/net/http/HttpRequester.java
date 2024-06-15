package latte.lib.common.net.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import latte.lib.common.net.Requester;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;


public class HttpRequester implements Requester {
  RestOperations restOperations;

  public HttpRequester() {
    this(100, 100, 2000, 5000);
  }
  public HttpRequester(
      int maxConnPerRoute,
      int maxConnTotal,
      int connectTimeout,
      int soTimeout
  ) {
    this.restOperations = createCommonsHttpRestTemplate(maxConnPerRoute, maxConnTotal, connectTimeout, soTimeout);
  }

  private static ObjectMapper createObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    return objectMapper;

  }
  RestOperations createCommonsHttpRestTemplate(
      int maxConnPerRoute,
      int maxConnTotal,
      int connectTimeout,
      int soTimeout

      ) {
    HttpClient httpClient = HttpClientBuilder.create()
        .setMaxConnPerRoute(maxConnPerRoute)
        .setMaxConnTotal(maxConnTotal)
        .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(soTimeout).build())
        .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).build())
        .build();
    ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    RestTemplate restTemplate = new RestTemplate(factory);

    List<HttpMessageConverter<?>> converters = new ArrayList<>();
    //set jackson mapper
    for (HttpMessageConverter<?> hmc: restTemplate.getMessageConverters()) {
      if (hmc instanceof MappingJackson2HttpMessageConverter) {
        ObjectMapper objectMapper = createObjectMapper();
        MappingJackson2HttpMessageConverter mj2hmc = (MappingJackson2HttpMessageConverter) hmc;
        mj2hmc.setObjectMapper(objectMapper);
      }
      if (!(hmc instanceof MappingJackson2XmlHttpMessageConverter)) {
        converters.add(hmc);
      }
    }

    restTemplate.setMessageConverters(converters);
    return restTemplate;
  }
  @Override
  public <T> T request(Object requestInfo, Class<T> glass) throws Exception {
    HttpRequestInfo info = (HttpRequestInfo) requestInfo;
    ResponseEntity<T> response = restOperations.exchange(
        info.getPath(),
        info.getMethod(),
        info.getHttpEntity(),
        glass
    );
    return response.getBody();
  }

  @Override
  public <T> List<T> requestList(Object requestInfo, Class<T> glass) throws Exception {
    HttpRequestInfo info = (HttpRequestInfo) requestInfo;
    ResponseEntity<List<T>> response = restOperations.exchange(
        info.getPath(),
        info.getMethod(),
        info.getHttpEntity(),
        new ParameterizedTypeReference<List<T>>() {
        }
    );
    return response.getBody();
  }

  @Override
  public <K, V> Map<K, V> requestMap(Object requestInfo, Class<K> k, Class<V> v) throws Exception {
    HttpRequestInfo info = (HttpRequestInfo) requestInfo;
    ResponseEntity<Map<K,V>> response = restOperations.exchange(
        info.getPath(),
        info.getMethod(),
        info.getHttpEntity(),
        new ParameterizedTypeReference<Map<K,V>>() {
        }
    );
    return response.getBody();
  }
}
