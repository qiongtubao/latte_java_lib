package latte.lib.tikv.log;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.tikv.common.log.*;

import java.util.Map;

public class LatteSlowLog implements SlowLog {

    private static final Logger logger = LoggerFactory.getLogger(SlowLogImpl.class);

    private static final int MAX_SPAN_SIZE = 1024;

    public LatteSlowLog() {

    }

    @Override
    public SlowLogSpan start(String name) {
        SlowLogSpan slowLogSpan = new LatteSlowLogSpan(name);
        slowLogSpan.start();
        return slowLogSpan;
    }

    @Override
    public long getTraceId() {
        return 0;
    }

    @Override
    public long getThresholdMS() {
        return 0;
    }

    @Override
    public void setError(Throwable throwable) {

    }

    @Override
    public SlowLog withFields(Map<String, Object> map) {
        return this;
    }

    @Override
    public Object getField(String s) {
        return null;
    }

    @Override
    public void log() {

    }

    static public void region() {
        SlowLogEmptyImpl.INSTANCE = new LatteSlowLog();
    }
}
