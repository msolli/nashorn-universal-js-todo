var path = require("path");
var webpack = require("webpack");

function isProduction() {
    return process.env.WEBPACK_ENV === "production";
}

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
        todo: "./todo/index.js"
    },
    output: {
        path: __dirname + "/public/dist/",
        filename: "[name].js"
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
