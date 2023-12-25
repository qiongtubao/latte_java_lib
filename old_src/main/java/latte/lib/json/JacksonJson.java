package latte.lib.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonJson implements Json {
    public <T> String encode(T t) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
    }
    public <T> T decode(String str, Class<T> glass) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        T r = objectMapper.readValue(str, glass);
        return r;
    }
}
