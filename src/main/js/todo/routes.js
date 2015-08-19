import { Route } from "react-router";
import { Index, Yo } from "./components";

export default function () {
    return (
        <Route path="/" component={Index}>
            <Route path="/yo" component={Yo}/>
        </Route>
    );
};
