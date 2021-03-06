var path = require("path");
var webpack = require("webpack");

var plugins = (function (plugins) {
    const PRODUCTION = "production" === process.env.WEBPACK_ENV;

    plugins.push(new webpack.DefinePlugin({
        "process.env.NODE_ENV": JSON.stringify(process.env.WEBPACK_ENV || "development")
    }));

    if (PRODUCTION) {
        plugins.push(new webpack.optimize.UglifyJsPlugin());
    }

    return plugins;
})([]);

module.exports = {
    context: path.resolve(__dirname, __dirname + "/src/main/js/"),
    entry: {
        todo: "./client/index.js"
    },
    output: {
        path: __dirname + "/public/dist/",
        filename: "[name]-client.js"
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: "babel"
            }
        ]
    },
    plugins: plugins
};
