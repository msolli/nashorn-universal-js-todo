import React from "react";
import TodoApp from "./app";
import TodoModel from "./model";

export default function (data) {
    const data_ = JSON.parse(data);
    const todos = data_["todos"] || [];
    const model = new TodoModel(todos);

    return React.renderToString(<TodoApp model={model}/>)
};
