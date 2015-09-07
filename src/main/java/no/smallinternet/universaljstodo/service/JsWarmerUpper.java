package no.smallinternet.universaljstodo.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.NashornExecutor;

public final class JsWarmerUpper {
    private static final Logger LOG = LoggerFactory.getLogger(JsWarmerUpper.class);
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private final ThreadLocal<Long> runs = ThreadLocal.withInitial(() -> 0l);
    private final NashornExecutor<JsComponents> executor;
    private final long targetDuration;
    private final int maxRuns;

    private JsWarmerUpper(Builder builder) {
        executor = builder.executor;
        targetDuration = builder.targetDuration;
        maxRuns = builder.maxRuns;
    }

    private void run() {
        pool.submit(new TodoAppWarmer(executor));
    }

    public static Builder create(NashornExecutor<JsComponents> executor) {
        return new JsWarmerUpper.Builder(executor);
    }

    public static class Builder {
        // Required parameters
        private final NashornExecutor<JsComponents> executor;

        // Optional parameters
        long targetDuration = 10; // milliseconds
        int maxRuns = 1000;

        public Builder(NashornExecutor<JsComponents> executor) {
            this.executor = executor;
        }

        public Builder targetDuration(int targetDuration, TimeUnit unit) {
            this.targetDuration = TimeUnit.MILLISECONDS.convert(targetDuration, unit);
            return this;
        }

        public Builder maxRuns(int maxRuns) {
            this.maxRuns = maxRuns;
            return this;
        }

        public void run() {
            new JsWarmerUpper(this).run();
        }
    }

    private <T, S> Function<T, S> withTimer(Function<T, S> fn, Runnable warmer) {
        return t -> {
            final String name = warmer.getClass().getSimpleName();
            final long start = System.nanoTime();
            try {
                return fn.apply(t);
            } finally {
                runs.set(runs.get() + 1l);
                LOG.debug("Run {} on {} for {}", runs.get(), Thread.currentThread().getName(), name);
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                if (elapsed <= targetDuration) {
                    LOG.debug("Warmup of {} complete", name);
                    pool.shutdown();
                } else if (runs.get() == maxRuns) {
                    LOG.debug("Warmup of {} failed, could not reach target duration after {} runs", name, maxRuns);
                    pool.shutdown();
                } else {
                    LOG.debug("Keep warming up {} ({} ms)", name, elapsed);
                    pool.submit(warmer);
                }
            }
        };
    }

    private class TodoAppWarmer implements Runnable {
        private final NashornExecutor<JsComponents> executor;
        private final String data = "{\"jobId\":674947,\"companyId\":14293592,\"profilePhoto\":\"foo.png\",\"canUploadFiles\":true," +
                "\"showInterestSource\":\"MY_COMPANY\",\"reviews\":[],\"templates\":[]}";

        public TodoAppWarmer(NashornExecutor<JsComponents> executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            executor.render(withTimer(js -> js.renderTodoApp(data), this), "");
        }

    }
}
