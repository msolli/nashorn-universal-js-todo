package no.smallinternet.universaljstodo.domain;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class TodoLoader {
    private static final Logger LOG = LoggerFactory.getLogger(TodoLoader.class);

    @Autowired
    public TodoLoader(TodoRepository repository) {
        LOG.info("Loading Todo application data");

        String[] items = {"Turn on", "Tune in", "Drop out"};
        repository.save(Arrays.stream(items)
                .map(i -> new Todo(i))
                .collect(toList()));
    }
}
