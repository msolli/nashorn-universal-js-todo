import React from "react";

const Yo = React.createClass({
    render() {
        return <div>Yo {this.props.yo}</div>;
    }
});

function renderTodoApp(data, location) {
    // TODO location / router stuff
    return React.renderToString(<Yo {...JSON.parse(data)}/>)
}

function initTodoApp(data, element) {
    console.log("initTodoApp", ...arguments);
    return React.render(<Yo {...data}/>, element);
}

export {renderTodoApp, initTodoApp};
