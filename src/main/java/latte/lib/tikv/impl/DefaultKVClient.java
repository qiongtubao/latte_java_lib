package latte.lib.tikv.impl;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.api.TransactionalCommands;
import latte.lib.tikv.api.transactional.TransactionAction;
import latte.lib.tikv.api.transactional.TransactionEvent;
import org.tikv.common.BytePairWrapper;
import org.tikv.common.ByteWrapper;
import org.tikv.common.TiConfiguration;
import org.tikv.common.TiSession;
import org.tikv.common.util.BackOffer;
import org.tikv.common.util.ConcreteBackOffer;
import org.tikv.shade.com.google.protobuf.ByteString;
import org.tikv.txn.TwoPhaseCommitter;
import org.tikv.txn.TxnKVClient;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultKVClient extends AbstractKVClient {
    TiSession session;
    public DefaultKVClient(TiSession session) {
        this.session = session;
    }
    KVTransaction beginOptimistic() {
        return new KVTransaction(session);
    }
    @Override
    public void exec(TransactionAction action) {
        KVTransaction transaction = this.beginOptimistic();
        action.doTxnAction(transaction);
        transaction.commit();
    }

    @Override
    public long getCurrentVersion() {
        return session.getTimestamp().getVersion();
    }

    @Override
    public void benchPut(Map<ByteString, ByteString> keyValues, long version) {
        KVTransaction transaction = this.beginOptimistic();
        transaction.version = version;
        keyValues.forEach((key, value)-> {
            transaction.putEvent(TransactionEvent.TransactionEventType.PUT, key, value);
        });
        transaction.commit();
    }

    @Override
    public List<ByteString> benchGet(List<ByteString> keys, long version) {
        org.tikv.txn.KVClient kvClient = session.createKVClient();
        List<ByteString> result = new LinkedList<>();
        keys.forEach(key-> {
            result.add(kvClient.get(key, version));
        });
        return result;
    }

    @Override
    public void benchDelete(List<ByteString> keys, long version) {

    }

    @Override
    public void deleteRange(ByteString start, ByteString end, long version) {

    }
}
