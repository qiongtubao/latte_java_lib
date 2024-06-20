package latte.lib.api.kv.scan;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractScanIterator<T> implements Iterator<T> {

  protected String startKey;
  protected String endKey;

  protected int limit;

  public AbstractScanIterator(String startKey, String endKey, int limit) {
      this.startKey = startKey;
      this.endKey = endKey;
      this.limit = limit;
  }

  protected List<T> data = new LinkedList<>();

  protected abstract int queryData();
  @Override
  public boolean hasNext() {
    if (data.size() > 0) {
      return true;
    }
    if (queryData() > 0) {
      return true;
    }
    return false;
  }

  @Override
  public T next() {
    return data.remove(0);
  }
}
