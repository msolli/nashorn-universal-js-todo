import React from "react";
//import { Router, Route, Link } from "react-router";
//import routes from "./routes";

const Yo = React.createClass({
    render() {
        return <div>Yo {this.props.yo}</div>;
    }
});

export default function (data, path, queryString) {
    return React.renderToString(<Yo {...JSON.parse(data)}/>)
};
