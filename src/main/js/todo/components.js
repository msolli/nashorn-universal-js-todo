import React from "react";
import { Link } from "react-router";

export const Index = React.createClass({
    render() {
        return (
            <div>
                <h1><Link to="/yo">Yo</Link></h1>
            </div>
        );
    }
});

export const Yo = React.createClass({
    render() {
        return (
            <div><h1><Link to="/">YO!</Link></h1></div>
        );
    }
});
