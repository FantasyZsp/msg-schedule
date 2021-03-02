package xyz.mydev.msg.common.util;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZSP
 */
public class PrefixNameThreadFactory implements ThreadFactory {
  private final String prefix;

  public PrefixNameThreadFactory(String prefix) {
    Objects.requireNonNull(prefix, "prefix not be null");
    this.prefix = prefix;
  }

  private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

  @Override
  public Thread newThread(Runnable r) {
    Objects.requireNonNull(r);
    return new Thread(r, prefix + "-" + ATOMIC_INTEGER.getAndIncrement());
  }
}