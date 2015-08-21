package renderer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.Test;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static javax.script.ScriptContext.ENGINE_SCOPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

public class NashornRendererTest {

    public interface IFoo<T> {
        T onePlusOne();
    }

    public interface Foo extends IFoo<Supplier<Integer>> {}

    @Test
    public void testConcurrency() throws Exception {
        final String[] jsFiles = {"/testConcurrency.js"};

//        NashornRenderer.Builder<Foo, Supplier<Integer>> builder =
//                new NashornRenderer.Builder<>(Foo.class, getFiles(jsFiles), 0)
//                        .poolSize(2);
//        Foo jsRenderer = builder.build();
//
//        ExecutorService executor = Executors.newCachedThreadPool();
//        ArrayList<Future<Integer>> results = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++) {
//            results.add(executor.submit((Callable) () -> jsRenderer.onePlusOne().get()));
//        }
//
//        for (Future<Integer> result : results) {
//            assertThat("Concurrency is not working properly", result.get(), is(2));
//        }
//
//        executor.awaitTermination(1, TimeUnit.SECONDS);
//        executor.shutdownNow();
    }

    private List<File> getFiles(String[] files) {
        return stream(files)
                .map(f -> this.getClass().getResource(f))
                .map(URL::getFile)
                .map(File::new)
                .collect(toList());

    }

    @Test
    public void testEngineInstances() throws Exception {
        final NashornScriptEngineFactory engineFactory = new NashornScriptEngineFactory();
        final NashornScriptEngine engine1 = (NashornScriptEngine) engineFactory.getScriptEngine();
        final NashornScriptEngine engine2 = (NashornScriptEngine) engineFactory.getScriptEngine();
        assertThat(engine1, not(sameInstance(engine2)));
        assertThat(engine1.getBindings(ENGINE_SCOPE), not(equalTo(engine2.getBindings(ENGINE_SCOPE))));
        assertThat(engine1.getBindings(ENGINE_SCOPE), not(sameInstance(engine2.getBindings(ENGINE_SCOPE))));
    }
}
