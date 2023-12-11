package latte.lib.db.example.tikv.api;

import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.Transaction;
import latte.lib.tikv.api.transactional.TransactionAction;
import latte.lib.tikv.api.TransactionalCommands;
import org.junit.Test;

public class TransactionTest {
    @Test
    public void transaction() {
        Transaction client = KVClientFactory.getKVClientByClusterName("local");
        client.exec(new TransactionAction() {
            long updateVersion = 2;
            long expectVersion = 1;
            boolean isDeleted = false;
            String value = "v2";
            @Override
            public void doTxnAction(TransactionalCommands cmd) {
                String version_str = cmd.hget("masterkey", "version");
                long version =  version_str == null? 0 : Long.valueOf(version_str);
                if (version == 0 || (version == expectVersion  && updateVersion > version)) {
                    if (isDeleted) {
                        cmd.hmdel("masterkey", "value");
                    } else {
                        cmd.hmset("masterkey","version", String.valueOf(updateVersion), "value", value);
                    }
                } else {
                    throw new RuntimeException("update fail");
                }
            }
        });
    }
}
