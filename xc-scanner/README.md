# Cordova Plugin XC Scanner
 Cordova Plugin MovFast Ranger2 Device

## Supported Platforms

- Android

## Installation

```bash
cordova plugin add com.xcscanner
```

## Usage

### scan

```js
const xcScanner = window.cordova.plugins.CordovaPluginXcScanner;

xcScanner.scan(
    data => {
      console.log('## XC Scanner barcode received: ', data);
      console.log('barcode: ', data.barcode);
    },
    error => {
      console.log('## XC Scanner error: ', error);
    },
);
```
