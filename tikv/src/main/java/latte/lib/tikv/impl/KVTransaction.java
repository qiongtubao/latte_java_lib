package latte.lib.tikv.impl;


import latte.lib.tikv.api.transactional.TransactionEvent;
import latte.lib.tikv.commiter.Committer;
import latte.lib.tikv.commiter.KVTwoPhaseCommiter;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.tikv.common.BytePairWrapper;
import org.tikv.common.ByteWrapper;
import org.tikv.common.TiSession;
import org.tikv.common.util.BackOffer;
import org.tikv.common.util.ConcreteBackOffer;

import org.tikv.shade.com.google.protobuf.ByteString;

import org.tikv.txn.KVClient;
import org.tikv.txn.TwoPhaseCommitter;
import org.tikv.txn.TxnKVClient;

import java.util.*;

public class KVTransaction extends AbstractTransactionCommands {
    static Logger logger = LoggerFactory.getLogger(KVTransaction.class);
    long version;
    TxnKVClient client;
    KVClient readClient;
    public static class ValueEntry {
        TransactionEvent.TransactionEventType event;
        ByteString value;
        public ValueEntry(TransactionEvent.TransactionEventType event, ByteString value) {
            this.event = event;
            this.value = value;
        }

        public ByteString getValue() {
            return value;
        }
    }

    @Setter
    @Getter
    static public class TransactionConfig {
        int backOfferMs = 2000;
        public TransactionConfig() {

        }

    }
    TiSession session;
    Map<ByteString, ValueEntry> buffer = new LinkedHashMap<>();
    TransactionConfig config = new TransactionConfig();
    public KVTransaction(TiSession session) {
        this.session = session;
        this.client = session.createTxnClient();
        this.version = client.getTimestamp().getVersion();
       logger.info(Thread.currentThread().getName() + " start tm " + this.version);
        this.readClient = session.createKVClient();
    }
    @Override
    public void putEvent(TransactionEvent.TransactionEventType type, ByteString key, ByteString value) {
        ValueEntry entry = buffer.get(key);
        if (entry == null) {
            buffer.put(key, new ValueEntry(type, value));
            return;
        }
        if (entry.event.equals(TransactionEvent.TransactionEventType.PUT)
                || entry.event.equals(TransactionEvent.TransactionEventType.Cached)) {
            buffer.put(key, new ValueEntry(type, value));
        }
    }

    public Committer getCommiter() {
        return new KVTwoPhaseCommiter(session, buffer, version, 4000);
    }

    public void commit() {
//        TwoPhaseCommitter twoPhaseCommitter = new TwoPhaseCommitter(session, version, 4000);
//
//        BackOffer backOffer = ConcreteBackOffer.newCustomBackOff(config.getBackOfferMs());
//        byte[] primaryKey = null;
//        ByteString key = null;
//        Iterator<ByteString> iterator = this.buffer.keySet().iterator();
//        if (iterator.hasNext()) {
//            key = iterator.next();
//            primaryKey = key.toByteArray();
//            twoPhaseCommitter.prewritePrimaryKey(backOffer, primaryKey, this.buffer.get(key).value.toByteArray());
//        }
//        List<BytePairWrapper> pairs = new LinkedList<>();
//        List<ByteWrapper> keys = new LinkedList<>();
//        while (iterator.hasNext()) {
//            key = iterator.next();
//            byte[] byteKey = key.toByteArray();
//            pairs.add(new BytePairWrapper(byteKey, this.buffer.get(key).value.toByteArray()));
//            keys.add(new ByteWrapper(byteKey));
//        }
//        twoPhaseCommitter.prewriteSecondaryKeys(primaryKey, pairs.iterator(), config.getBackOfferMs());
//        long commitVersion = session.getTimestamp().getVersion();
//        logger.info(Thread.currentThread().getName() + " commit tm " + commitVersion);
//        twoPhaseCommitter.commitPrimaryKey(backOffer, primaryKey, commitVersion);
//        twoPhaseCommitter.commitSecondaryKeys(keys.iterator(), commitVersion, config.getBackOfferMs());

        Committer committer = getCommiter();
        committer.prewrite(config.getBackOfferMs());
        committer.commit(config.getBackOfferMs());
    }


    public ByteString loadValue(ByteString key) {
        //不一定能获得对的version
        return readClient.get(key, version);
    }
    @Override
    public ByteString getOrLoadValue(ByteString key) {
        ValueEntry entry = buffer.get(key);
        if (entry != null) {
            if (entry.event.equals(TransactionEvent.TransactionEventType.PUT)
                || entry.event.equals(TransactionEvent.TransactionEventType.Cached)
                || entry.event.equals(TransactionEvent.TransactionEventType.Insert)) {
                return entry.value;
            }
            if (entry.event.equals(TransactionEvent.TransactionEventType.DELETE)
            || entry.event.equals(TransactionEvent.TransactionEventType.CheckNotExist)) {
                return null;
            }
        }
        ByteString value = loadValue(key);
        putEvent(TransactionEvent.TransactionEventType.Cached, key, value);
        return value;
    }
}
