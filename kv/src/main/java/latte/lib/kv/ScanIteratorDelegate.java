package latte.lib.kv;

import latte.lib.api.kv.scan.AbstractScanIterator;
import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;

public class ScanIteratorDelegate<T> extends AbstractScanIterator<T> {

  AbstractScanIterator<T> scanIterator;

  Monitor monitor;
  public ScanIteratorDelegate(
        AbstractScanIterator<T> scanIterator,
        Monitor monitor
  ) {
    super(scanIterator.getStartKey(), scanIterator.getEndKey(), scanIterator.getLimit());
    this.monitor = monitor;
  }

  @Override
  public boolean hasNext() {
    return scanIterator.hasNext();
  }

  @Override
  public T next() {
    return scanIterator.next();
  }

  @Override
  public int queryData() {
    Transaction transaction = monitor.getTransaction(this.scanIterator.getClass().getSimpleName() +".queryData");
    int result = 0;
    try {
      result = scanIterator.queryData();
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return result;
  }
}
