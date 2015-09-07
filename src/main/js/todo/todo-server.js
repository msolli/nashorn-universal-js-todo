import React from "react";

const Yo = React.createClass({
    render() {
        return <div>Yo {this.props.yo}</div>;
    }
});

export default function (data) {
    return React.renderToString(<Yo {...JSON.parse(data)}/>)
};
