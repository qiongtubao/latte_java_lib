package latte.lib.kv;

import latte.lib.api.kv.scan.AbstractScanIterator;
import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanIteratorDelegate<T> extends AbstractScanIterator<T> {

  AbstractScanIterator<T> scanIterator;

  Monitor monitor;

  boolean isEnd = false;
  public ScanIteratorDelegate(
        AbstractScanIterator<T> scanIterator,
        Monitor monitor
  ) {
    super(scanIterator.getStartKey(), scanIterator.getEndKey(), scanIterator.getLimit());
    this.monitor = monitor;
    this.scanIterator = scanIterator;
  }



  @Override
  public T next() {
    return scanIterator.next();
  }
  static Logger logger = LoggerFactory.getLogger(ScanIteratorDelegate.class);

  @Override
  public int queryData() {
    throw new RuntimeException("test");
  }

  @Override
  public boolean hasNext() {
    if (scanIterator.getData().size() > 0) {
      return true;
    }
    if (isEnd) {
      return false;
    }
    Transaction transaction = monitor.getTransaction(this.scanIterator.getClass().getSimpleName() + ".scanQueryData");
    try {
      int result = scanIterator.queryData();
      if (result < scanIterator.getLimit()) {
        isEnd = true;
      }
      if (result > 0) {
        return true;
      }
    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return false;
  }
}
