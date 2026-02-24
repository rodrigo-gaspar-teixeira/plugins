# Cordova Plugin CPX Scanner
 Cordova Plugin CPX100 Device - (Plugin was implemented and never tested in the right device)

## Supported Platforms

- Android

## Installation

```bash
cordova plugin add com.cpxscanner
```

## Usage

### scan

```js
const cpxReader = window.cordova.plugins.CordovaPluginCpxScanner;

cpxReader.scan(
    data => {
      console.log('## CPX Scanner barcode received: ', data);
      console.log('barcode: ', data.barcode);
    },
    error => {
      console.log('## CPX Scanner error: ', error);
    },
);
```