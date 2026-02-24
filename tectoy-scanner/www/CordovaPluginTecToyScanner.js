var exec = require('cordova/exec');

module.exports = {
    scan: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CordovaPluginTecToyScanner', 'scan', []);
    }
};