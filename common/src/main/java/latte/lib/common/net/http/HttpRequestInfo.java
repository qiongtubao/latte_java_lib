package latte.lib.common.net.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@Setter
@Getter
public class HttpRequestInfo<T> {
    String path;
    HttpMethod method;
    HttpEntity<T> httpEntity;

    public HttpRequestInfo(
        String path,
        HttpMethod method,
        HttpEntity httpEntity
        ) {
        this.path = path;
        this.method = method;
        this.httpEntity = httpEntity;
    }
}
