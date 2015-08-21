package renderer;

import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NashornExecutor<T> {
    private static final Logger LOG = LoggerFactory.getLogger(NashornExecutor.class);
    private final ThreadLocal<T> supplier;
    private final ExecutorService pool;
    private final int timeout;

    // TODO
    // - builder pattern
    // - set default timeout to something sane (10s?, 100ms?)


    public NashornExecutor(Supplier<T> supplier, ExecutorService pool, int timeout) {
        this.supplier = ThreadLocal.withInitial(supplier);
        this.pool = pool;
        this.timeout = timeout;
    }

    public <S> Supplier<S> render(Function<T, S> fn, S defaultValue) {
        try {
            final Future<S> future = pool.submit(() -> fn.apply(supplier.get()));
            try {
                if (timeout > 0) {
                    final S result = future.get(timeout, TimeUnit.MILLISECONDS);
                    return () -> result;
                } else {
                    final S result = future.get();
                    return () -> result;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOG.error("Could not render JS", e);
            } catch (TimeoutException e) {
                LOG.warn("JS rendering timed out");
            }
        } catch (RejectedExecutionException e) {
            LOG.error("JS execution rejected", e);
        }
        return () -> defaultValue;
    }

}
