import React from "react";
import { Router } from "director";
import TodoItem from "./todoItem";
import TodoFooter from "./footer";
import { ALL_TODOS, ACTIVE_TODOS, COMPLETED_TODOS } from "./constants";

const ENTER_KEY = 13;

export default React.createClass({
    getInitialState: function () {
        return {
            nowShowing: ALL_TODOS,
            editing: null
        };
    },

    componentDidMount: function () {
        var setState = this.setState;
        var router = new Router({
            '/': setState.bind(this, {nowShowing: ALL_TODOS}),
            '/active': setState.bind(this, {nowShowing: ACTIVE_TODOS}),
            '/completed': setState.bind(this, {nowShowing: COMPLETED_TODOS})
        });
        router.init('/');
    },

    handleNewTodoKeyDown: function (event) {
        if (event.keyCode !== ENTER_KEY) {
            return;
        }

        event.preventDefault();

        var val = React.findDOMNode(this.refs.newField).value.trim();

        if (val) {
            this.props.model.addTodo(val);
            React.findDOMNode(this.refs.newField).value = '';
        }
    },

    toggleAll: function (event) {
        var checked = event.target.checked;
        this.props.model.toggleAll(checked);
    },

    toggle: function (todoToToggle) {
        this.props.model.toggle(todoToToggle);
    },

    destroy: function (todo) {
        this.props.model.destroy(todo);
    },

    edit: function (todo) {
        this.setState({editing: todo.id});
    },

    save: function (todoToSave, text) {
        this.props.model.save(todoToSave, text);
        this.setState({editing: null});
    },

    cancel: function () {
        this.setState({editing: null});
    },

    clearCompleted: function () {
        this.props.model.clearCompleted();
    },

    render: function () {
        var footer;
        var main;
        var todos = this.props.model.todos;

        var shownTodos = todos.filter(function (todo) {
            switch (this.state.nowShowing) {
                case ACTIVE_TODOS:
                    return !todo.completed;
                case COMPLETED_TODOS:
                    return todo.completed;
                default:
                    return true;
            }
        }, this);

        var todoItems = shownTodos.map(function (todo) {
            return (
                <TodoItem
                    key={todo.id}
                    todo={todo}
                    onToggle={this.toggle.bind(this, todo)}
                    onDestroy={this.destroy.bind(this, todo)}
                    onEdit={this.edit.bind(this, todo)}
                    editing={this.state.editing === todo.id}
                    onSave={this.save.bind(this, todo)}
                    onCancel={this.cancel}
                    />
            );
        }, this);

        var activeTodoCount = todos.reduce(function (accum, todo) {
            return todo.completed ? accum : accum + 1;
        }, 0);

        var completedCount = todos.length - activeTodoCount;

        if (activeTodoCount || completedCount) {
            footer =
                <TodoFooter
                    count={activeTodoCount}
                    completedCount={completedCount}
                    nowShowing={this.state.nowShowing}
                    onClearCompleted={this.clearCompleted}
                    />;
        }

        if (todos.length) {
            main = (
                <section className="main">
                    <input
                        className="toggle-all"
                        type="checkbox"
                        onChange={this.toggleAll}
                        checked={activeTodoCount === 0}
                        />
                    <ul className="todo-list">
                        {todoItems}
                    </ul>
                </section>
            );
        }

        return (
            <div>
                <header className="header">
                    <h1>todos</h1>
                    <input
                        ref="newField"
                        className="new-todo"
                        placeholder="What needs to be done?"
                        onKeyDown={this.handleNewTodoKeyDown}
                        autoFocus={true}
                        />
                </header>
                {main}
                {footer}
            </div>
        );
    }
});
