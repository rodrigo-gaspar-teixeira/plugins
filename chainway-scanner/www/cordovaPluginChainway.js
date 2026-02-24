var exec = require('cordova/exec');

// exports.coolMethod = function (arg0, success, error) {
//     exec(success, error, 'cordova-plugin-chainway', 'coolMethod', [arg0]);
// };

module.exports = {
    scan: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CordovaPluginChainway', 'scan', []);
    },
    cancel: function () {
        exec(null, null, 'CordovaPluginChainway', 'cancel', []);
    }
};

