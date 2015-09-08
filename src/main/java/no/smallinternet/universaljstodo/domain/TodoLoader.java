package no.smallinternet.universaljstodo.domain;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class TodoLoader implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(TodoLoader.class);

    @Autowired
    private TodoRepository repository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Loading Todo application data");

        String[] items = {"Turn on", "Tune in", "Drop out"};
        repository.save(Arrays.stream(items)
                .map(i -> new Todo(i))
                .collect(toList()));
    }
}
