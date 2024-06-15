package latte.lib.common.net.http;

import org.springframework.retry.RetryPolicy;

public interface RetryPolicyFactory {
  public RetryPolicy create();
}
