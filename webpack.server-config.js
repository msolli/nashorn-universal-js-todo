var path = require("path");
var webpack = require("webpack");

var plugins = (function (plugins) {
    plugins.push(new webpack.DefinePlugin({
        "process.env.NODE_ENV": "\"production\""
    }));

    plugins.push(new webpack.optimize.UglifyJsPlugin({
        mangle: false,
        beautify: true,
        compress: {
            warnings: false
        }
    }));

    return plugins;
})([]);

module.exports = {
    context: path.resolve(__dirname, __dirname + "/src/main/js/"),
    entry: {
        todo: "./server/index.js"
    },
    output: {
        path: __dirname + "/public/dist/",
        filename: "[name]-server.js"
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
    resolve: {
        alias: {
            "director": "director/lib/director/router.js"
        }
    },
    plugins: plugins
};
