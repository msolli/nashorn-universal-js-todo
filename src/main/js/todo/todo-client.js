import React from "react";
import TodoApp from "./app";
import TodoModel from "./model";

export default function (data, element) {
    const todos = data["todos"] || [];
    const model = new TodoModel(todos);

    const render = function () {
        React.render(<TodoApp model={model}/>, element);
    };
    model.subscribe(render);
    render();
};
