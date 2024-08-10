package latte.lib.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import latte.lib.common.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteThreadFactory implements ThreadFactory {
  private static Logger logger = LoggerFactory.getLogger(LatteThreadFactory.class);

  protected final AtomicLong m_threadNumber = new AtomicLong(1);

  public static final int RANDOM_STRING_LEN = 5;

  protected final String m_namePrefix;

  protected final boolean m_daemon;

  private final static ThreadGroup m_threadGroup = new ThreadGroup("Latte");
  protected LatteThreadFactory(String namePrefix, boolean daemon) {
    m_namePrefix = namePrefix;
    m_daemon = daemon;
  }

  public static ThreadFactory create(String namePrefix) {
    return create(namePrefix, false);
  }

  public final static String PROFILE_KEY = "spring.profiles.active";

  public final static String PROFILE_NAME_PRODUCTION = "production";

  public final static String PROFILE_NAME_TEST = "test";
  private static String getThreadName(String namePrefix) {

    if(PROFILE_NAME_TEST.equals(System.getProperty(PROFILE_KEY))){
      return namePrefix + "-" + RandomUtils.randomAlphabeticString(RANDOM_STRING_LEN);
    }
    return namePrefix;
  }
  public static ThreadFactory create(String namePrefix, boolean daemon) {
    return new LatteThreadFactory(getThreadName(namePrefix), daemon);
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(m_threadGroup, r,//
        m_namePrefix + "-" + m_threadNumber.getAndIncrement());
    t.setDaemon(m_daemon);
    if (t.getPriority() != Thread.NORM_PRIORITY)
      t.setPriority(Thread.NORM_PRIORITY);
    return t;
  }
}
