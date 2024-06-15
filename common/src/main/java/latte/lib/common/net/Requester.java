package latte.lib.common.net;

import java.util.List;
import java.util.Map;

public interface Requester {
   <T> T request(Object requestInfo, Class<T> glass) throws Exception;

   <T> List<T> requestList(Object requestInfo, Class<T> glass) throws Exception;

   <K,V> Map<K,V> requestMap(Object requestInfo, Class<K> k, Class<V> v) throws Exception;


}
