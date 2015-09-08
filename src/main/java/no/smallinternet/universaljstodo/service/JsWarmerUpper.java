package no.smallinternet.universaljstodo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renderer.NashornExecutor;

public final class JsWarmerUpper {
    private static final Logger LOG = LoggerFactory.getLogger(JsWarmerUpper.class);
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private final ThreadLocal<Integer> runs = ThreadLocal.withInitial(() -> 0);
    private final NashornExecutor<JsComponents> executor;
    private final long targetDuration;
    private final int maxRuns;
    private List<WarmupListener> listeners;
    private ArrayList<Long> stats = new ArrayList<>();

    private JsWarmerUpper(Builder builder) {
        executor = builder.executor;
        targetDuration = builder.targetDuration;
        maxRuns = builder.maxRuns;
        listeners = builder.listeners;
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
        List<WarmupListener> listeners = new ArrayList<>();

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

        public Builder onComplete(WarmupListener listener) {
            this.listeners.add(listener);
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
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                stats.add(elapsed);
                LOG.debug("Run {} on {} for {}", runs.get(), Thread.currentThread().getName(), name);
                runs.set(runs.get() + 1);
                if (elapsed <= targetDuration) {
                    LOG.debug("Warmup of {} complete", name);
                    notifyComplete();
                    pool.shutdown();
                } else if (runs.get() == maxRuns) {
                    LOG.debug("Warmup of {} failed, could not reach target duration after {} runs", name, maxRuns);
                    notifyComplete();
                    pool.shutdown();
                } else {
                    LOG.debug("Keep warming up {} ({} ms)", name, elapsed);
                    pool.submit(warmer);
                }
            }
        };
    }

    private void notifyComplete() {
        final long[] statsArray = stats.stream()
                .mapToLong(s -> s)
                .toArray();
        for (WarmupListener listener : listeners) {
            listener.warmupComplete(new WarmupResult(statsArray));
        }
    }

    private class TodoAppWarmer implements Runnable {
        private final NashornExecutor<JsComponents> executor;
        private final String json;

        public TodoAppWarmer(NashornExecutor<JsComponents> executor) {
            this.executor = executor;
            final Map<String, Object> data = new HashMap<>();
            data.put("todos", new ArrayList<String>());
            this.json = toJson(data);
        }

        @Override
        public void run() {
            executor.render(withTimer(js -> js.renderTodoApp(json), this), "");
        }

    }

    private static String toJson(Object o) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
