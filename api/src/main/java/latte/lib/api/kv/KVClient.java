package latte.lib.api.kv;

public interface KVClient extends StringCommand, DelCommand, ScanCommand {
    void close() throws Exception;
}
