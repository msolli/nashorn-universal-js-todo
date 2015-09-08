package no.smallinternet.universaljstodo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private Boolean completed = Boolean.FALSE;

    protected Todo() {
    }

    public Todo(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return String.format(
                "Todo[id=%d, title='%s', completed=%b]",
                id, title, completed);
    }
}
