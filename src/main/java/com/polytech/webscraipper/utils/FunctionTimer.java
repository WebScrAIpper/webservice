package com.polytech.webscraipper.utils;

import com.polytech.webscraipper.BaseLogger;
import com.polytech.webscraipper.builders.DefaultBuilder;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class FunctionTimer {

  private static Map<String, Long> taskStartTimes = new ConcurrentHashMap<>();

  public static <T> T timeExecution(String taskName, Supplier<T> task) {
    long timeAtStart = System.currentTimeMillis();
    taskStartTimes.put(taskName, timeAtStart);
    ScheduledExecutorService scheduler = startDurationLogger(taskName);
    try {
      return task.get(); // Execute the function and return its result
    } finally {
      stopDurationLogger(scheduler, taskName);
    }
  }

  public static <T> T timeExecutionWithTimeout(
      String taskName, Supplier<T> task, long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException, ExecutionException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    taskStartTimes.put(taskName, System.currentTimeMillis());
    ScheduledExecutorService scheduler = startDurationLogger(taskName);

    Future<T> future = executor.submit(task::get);

    try {
      return future.get(timeout, unit); // Execute with timeout
    } catch (TimeoutException e) {
      future.cancel(true); // Attempt to interrupt the task
      throw new TimeoutException(
          taskName + " timed out after " + timeout + " " + unit.toString().toLowerCase());
    } finally {
      executor.shutdown();
      scheduler.shutdown();
      stopDurationLogger(scheduler, taskName);
    }
  }

  private static ScheduledExecutorService startDurationLogger(String taskName) {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    BaseLogger logger = new BaseLogger(DefaultBuilder.class);
    scheduler.scheduleAtFixedRate(
        () -> {
          long timeAtNow = System.currentTimeMillis();
          logger.warn(
              taskName
                  + " is running for "
                  + (timeAtNow - taskStartTimes.get(taskName)) / 1000
                  + " seconds.");
        },
        10,
        5,
        TimeUnit.SECONDS);
    return scheduler;
  }

  private static void stopDurationLogger(ScheduledExecutorService scheduler, String taskName) {
    scheduler.shutdown();
    long timeAtEnd = System.currentTimeMillis();
    BaseLogger logger = new BaseLogger(DefaultBuilder.class);
    logger.info(
        taskName
            + " completed in "
            + (timeAtEnd - taskStartTimes.get(taskName)) / 1000
            + " seconds.");
    taskStartTimes.remove(taskName);
  }
}
