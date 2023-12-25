package latte.lib.db.example.tikv;


import org.junit.Test;
import org.tikv.common.*;
import org.tikv.common.util.BackOffer;
import org.tikv.common.util.ConcreteBackOffer;
import org.tikv.kvproto.Kvrpcpb;
import org.tikv.raw.RawKVClient;
import org.tikv.shade.com.google.protobuf.ByteString;
import org.tikv.txn.KVClient;
import org.tikv.txn.TwoPhaseCommitter;
import org.tikv.txn.TxnKVClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TikvTest {
    @Test
    public void test() throws Exception {
        // You MUST create a raw configuration if you are using RawKVClient.
        TiConfiguration conf = TiConfiguration.createRawDefault("127.0.0.1:2379");
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        TiSession session = TiSession.create(conf);
        RawKVClient client = session.createRawClient();

        // put
        client.put(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("123"));
        client.put(ByteString.copyFromUtf8("b"), ByteString.copyFromUtf8("123"));
        client.put(ByteString.copyFromUtf8("aa"), ByteString.copyFromUtf8("123"));

        // get
//        Optional<ByteString> result = client.get(ByteString.copyFromUtf8("a"));
//        System.out.println(result.get().toStringUtf8());

        // batch get
//        List<Kvrpcpb.KvPair> list = client.batchGet(new ArrayList<ByteString>() {{
//            add(ByteString.copyFromUtf8("a"));
//            add(ByteString.copyFromUtf8("aa"));
//        }});
//        System.out.println(list);

        // scan
        List<Kvrpcpb.KvPair> list = client.scan(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("c"), 10);
        System.out.println(list);

        // close
        client.close();
        session.close();
    }

    @Test
    public void txn() throws Exception {
        TiConfiguration conf = TiConfiguration.createDefault("127.0.0.1:2379");
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        try (TiSession session = TiSession.create(conf)) {
            // two-phrase write
            long startTS = session.getTimestamp().getVersion();
            TxnKVClient client = session.createTxnClient();
            try (TwoPhaseCommitter twoPhaseCommitter = new TwoPhaseCommitter(session, startTS)) {
                BackOffer backOffer = ConcreteBackOffer.newCustomBackOff(1000);
                byte[] primaryKey = "ac".getBytes("UTF-8");
                byte[] key2 = "ad".getBytes("UTF-8");
                // first phrase: prewrite
                twoPhaseCommitter.prewritePrimaryKey(backOffer, primaryKey, "val1".getBytes("UTF-8"));
                List<BytePairWrapper> pairs = Arrays
                        .asList(new BytePairWrapper(key2, "val2".getBytes("UTF-8")));
                twoPhaseCommitter.prewriteSecondaryKeys(primaryKey, pairs.iterator(), 1000);
                // second phrase: commit
                long commitTS = session.getTimestamp().getVersion();
                twoPhaseCommitter.commitPrimaryKey(backOffer, primaryKey, commitTS);
                List<ByteWrapper> keys = Arrays.asList(new ByteWrapper(key2));
                twoPhaseCommitter.commitSecondaryKeys(keys.iterator(), commitTS, 1000);
            }

            try (KVClient kvClient = session.createKVClient()) {
                long version = session.getTimestamp().getVersion();
                ByteString key1 = ByteString.copyFromUtf8("ac");
                ByteString key2 = ByteString.copyFromUtf8("ad");

                // get value of a single key
                ByteString val = kvClient.get(key1, version);
                System.out.println(val);

                // get value of multiple keys
                BackOffer backOffer = ConcreteBackOffer.newCustomBackOff(1000);
                List<Kvrpcpb.KvPair> kvPairs = kvClient.batchGet(backOffer, Arrays.asList(key1, key2), version);
                System.out.println(kvPairs);

                // get value of a range of keys
                kvPairs = kvClient.scan(key1, ByteString.copyFromUtf8("ae"), version);
                System.out.println(kvPairs);
            }
        }
    }
}
