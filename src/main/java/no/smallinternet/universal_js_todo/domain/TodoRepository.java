package no.smallinternet.universal_js_todo.domain;

import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
//    Todo findOne(long id);
    Iterable<Todo> findAll();
}
