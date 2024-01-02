package latte.lib.redis.api;

public interface RedisCommand {
    Object execCommand(String... args);

    default String parseString(Object result) {
        if(result == null) {
            return null;
        }
        if (result.getClass().equals(String.class)) {
            return (String)result;
        }
        if (result instanceof byte[]) {
            return new String((byte[])result);
        }


        throw new RuntimeException("not support " + result.getClass() + "-> String");

    }
}
