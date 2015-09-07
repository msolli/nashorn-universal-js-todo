package renderer;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class NashornExecutor<T> {
    private static final Logger LOG = LoggerFactory.getLogger(NashornExecutor.class);
    private final ThreadLocal<T> supplier;
    private final ExecutorService pool;

    public NashornExecutor(Supplier<T> supplier, ExecutorService pool) {
        this.supplier = ThreadLocal.withInitial(supplier);
        this.pool = pool;
    }

    public <S> Supplier<S> render(Function<T, S> fn, S defaultValue, Integer timeout) {
        try {
            final Future<S> future = pool.submit(() -> fn.apply(supplier.get()));
            try {
                if (timeout != null) {
                    final S result = future.get(timeout, MILLISECONDS);
                    return () -> result;
                } else {
                    final S result = future.get();
                    return () -> result;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException | UndeclaredThrowableException e) {
                LOG.error("Could not render JS", e);
            } catch (TimeoutException e) {
                LOG.info("JS rendering timed out");
            }
        } catch (RejectedExecutionException e) {
            LOG.warn("JS execution rejected");
        }
        return () -> defaultValue;
    }

    public <S> Supplier<S> render(Function<T, S> fn, S defaultValue) {
        return render(fn, defaultValue, null);
    }
}
