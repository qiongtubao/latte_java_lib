package latte.lib.api.monitor;

public interface Transaction {
    Transaction addTag(String k, String v);
    Transaction setSuccess();
    Transaction setFail(Throwable e);
    Transaction complete();
}
