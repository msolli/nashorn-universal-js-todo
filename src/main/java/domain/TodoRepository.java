package domain;

import domain.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
    Todo findOne(long id);
    Iterable<Todo> findAll();
}
