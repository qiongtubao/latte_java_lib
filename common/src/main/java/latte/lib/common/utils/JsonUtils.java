package latte.lib.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static String encode(Object data) throws Exception {
        if(data.getClass().equals(String.class)) {
            return (String)data;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }

    public static <T> T decode(String data, Class<T> glass) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, glass);
    }

    public static <T> List<T> decodeList(String data, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, new TypeReference<List<T>>() {
            @Override
            public Type getType() {
                return objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            }
        });
    }

    public static <K,V> Map<K, V> decodeMap(String data, Class<K> klazz, Class<V> vlazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, new TypeReference<Map<K,V>>() {
            @Override
            public Type getType() {
                Type[] actualTypeArguments = new Type[]{klazz, vlazz};
                return ParameterizedTypeImpl.make(Map.class, actualTypeArguments, null);
            }
        });
    }


    public static String encodeException(Exception exception) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
    public static <T> T decodeJsonFile(String path, Class<T> glass) throws Exception {
        File file = new File(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, glass);
    }
}
