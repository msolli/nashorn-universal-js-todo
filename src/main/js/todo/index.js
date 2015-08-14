import React from "react";

const Yo = React.createClass({
    render() {
        return <div>Yo {this.props.yo}</div>;
    }
});

function renderTodoApp(data, location) {
    console.log("renderTodoApp:", ...arguments);
    // TODO location / router stuff
    let data_ = {};
    for (let k in data) {
        console.log(k, data[k]);
        if (data[k] instanceof Java.type("java.lang.Iterable")) {
            data_[k] = Java.from(data[k])
        } else {
            data_[k] = data[k]
        }
    }

    return React.renderToString(<Yo {...data_}/>)
}

function initTodoApp(data, element) {
    console.log("initTodoApp", ...arguments);
    return React.render(<Yo {...data}/>, element);
}

export {renderTodoApp, initTodoApp};
