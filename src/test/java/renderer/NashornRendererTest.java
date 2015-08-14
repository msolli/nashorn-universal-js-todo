package renderer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NashornRendererTest {

    public interface IFoo<T> {
        T onePlusOne();
    }

    public interface Renderer extends IFoo<Integer> {}

    private JsRenderer<Renderer> jsRenderer;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConcurrency() throws Exception {
        final String[] jsFiles = {"/testConcurrency.js"};

        NashornRenderer.Builder<Renderer> builder = new NashornRenderer.Builder<>(Renderer.class, getFiles(jsFiles))
                .disableAsyncInitialization();
        jsRenderer = builder.build();

        Callable<Integer> onePlusOne = () -> jsRenderer.getProxyObject().onePlusOne();

        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Future<Integer>> results = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            results.add(executor.submit(onePlusOne));
        }

        for (Future<Integer> result : results) {
            assertThat(result.get(), is(2));
        }

        executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdownNow();
    }

    private List<File> getFiles(String[] files) {
        return stream(files)
                .map(f -> this.getClass().getResource(f))
                .map(URL::getFile)
                .map(File::new)
                .collect(toList());

    }
}
