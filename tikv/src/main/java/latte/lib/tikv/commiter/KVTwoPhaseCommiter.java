package latte.lib.tikv.commiter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import latte.lib.tikv.impl.KVTransaction.ValueEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tikv.common.BytePairWrapper;
import org.tikv.common.ByteWrapper;
import org.tikv.common.TiSession;
import org.tikv.common.util.BackOffer;
import org.tikv.common.util.ConcreteBackOffer;
import org.tikv.shade.com.google.protobuf.ByteString;
import org.tikv.txn.TwoPhaseCommitter;

public class KVTwoPhaseCommiter implements Committer {

  Map<ByteString, ValueEntry> buffer;
  TiSession session;
  long version;
  int backOfferMs;
  TwoPhaseCommitter committer;
  Logger logger = LoggerFactory.getLogger(KVTwoPhaseCommiter.class);


  public KVTwoPhaseCommiter(TiSession session, Map<ByteString, ValueEntry> buffer, long version, long lockTTL) {
      this.session = session;
      this.buffer = buffer;
      this.version = version;
      committer = new TwoPhaseCommitter(session, version, lockTTL);
  }

  Iterator<ByteString> iterator;
  byte[] primaryKey;
  public void prewritePrimaryKey(int backOfferMs) {
    BackOffer backOffer = ConcreteBackOffer.newCustomBackOff(backOfferMs);
    iterator = this.buffer.keySet().iterator();
    if (iterator.hasNext()) {
      ByteString key = iterator.next();
      primaryKey = key.toByteArray();
      committer.prewritePrimaryKey(backOffer, primaryKey, this.buffer.get(key).getValue().toByteArray());
    }
    logger.info("prewrite {}", new String(primaryKey));
  }
  List<BytePairWrapper> pairs = new LinkedList<>();
  List<ByteWrapper> keys = new LinkedList<>();
  public void prewriteSecondaryKeys(int backOfferMs) {
    ByteString key;
    while (iterator.hasNext()) {
      key = iterator.next();
      byte[] byteKey = key.toByteArray();
      pairs.add(new BytePairWrapper(byteKey, this.buffer.get(key).getValue().toByteArray()));
      keys.add(new ByteWrapper(byteKey));
    }
    committer.prewriteSecondaryKeys(primaryKey, pairs.iterator(), backOfferMs);
  }

  @Override
  public void prewrite(int backOfferMs) {

    prewritePrimaryKey(backOfferMs);
    prewriteSecondaryKeys(backOfferMs);
  }

  long commitVersion;
  public void commitPrimaryKey(int backOfferMs)  {
    BackOffer backOffer = ConcreteBackOffer.newCustomBackOff(backOfferMs);
    commitVersion = session.getTimestamp().getVersion();
    committer.commitPrimaryKey(backOffer, primaryKey, commitVersion);
  }

  public void commitSecondaryKeys(int backOfferMs) {
    committer.commitSecondaryKeys(keys.iterator(), commitVersion, backOfferMs);
  }

  public long getVersion() {
    return session.getTimestamp().getVersion();
  }

  @Override
  public void commit(int backOfferMs) {
    commitPrimaryKey(backOfferMs);
    commitSecondaryKeys(backOfferMs);
  }
}
