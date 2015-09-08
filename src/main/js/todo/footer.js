import React from "react";
import cx from "classnames";
import Utils from "./utils";
import { ALL_TODOS, ACTIVE_TODOS, COMPLETED_TODOS } from "./constants";

export default React.createClass({
    render: function () {
        var activeTodoWord = Utils.pluralize(this.props.count, 'item');
        var clearButton = null;

        if (this.props.completedCount > 0) {
            clearButton = (
                <button
                    className="clear-completed"
                    onClick={this.props.onClearCompleted}>
                    Clear completed
                </button>
            );
        }

        // React idiom for shortcutting to `classSet` since it'll be used often
        var nowShowing = this.props.nowShowing;
        return (
            <footer className="footer">
					<span className="todo-count">
						<strong>{this.props.count}</strong> {activeTodoWord} left
					</span>
                <ul className="filters">
                    <li>
                        <a
                            href="#/"
                            className={cx({selected: nowShowing === ALL_TODOS})}>
                            All
                        </a>
                    </li>
                    {' '}
                    <li>
                        <a
                            href="#/active"
                            className={cx({selected: nowShowing === ACTIVE_TODOS})}>
                            Active
                        </a>
                    </li>
                    {' '}
                    <li>
                        <a
                            href="#/completed"
                            className={cx({selected: nowShowing === COMPLETED_TODOS})}>
                            Completed
                        </a>
                    </li>
                </ul>
                {clearButton}
            </footer>
        );
    }
});
