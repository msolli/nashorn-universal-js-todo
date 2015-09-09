# Server-side rendering of React on Nashorn

This is an example app that demonstrates an approach for doing server-side rendering of [React](http://facebook.github.io/react/) on [Nashorn](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/).

Some features demonstrated herein:
* A Java interface of the available React (top-level) components, and implement that interface in JavaScript.
* A service, with multiple threads for doing the rendering.
* A mechanism for warming up the JS code so it performs well.
* A timeout for the rendering, so we can serve up "empty" HTML if the JS takes to long to run.
* Reloading of JS code in development mode without restarting the app.

It's neither complete nor finished. Ideas for improvement and pull requests welcome.

### License

The MIT License (MIT)

Copyright © 2015 Martin Solli

[TodoMVC](https://github.com/tastejs/todomvc) code is © its authors
