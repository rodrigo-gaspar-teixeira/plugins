# Cordova Plugin TecToy Scanner
 Cordova Plugin TecToy Device Scanner

## Supported Platforms

- Android

## Installation

```bash
cordova plugin add com.tectoyscanner
```

## Usage

### scan

```js
const tectoy = window.cordova.plugins.CordovaPluginTecToyScanner

tectoy.scan(
    data => {
      console.log('## TecToy Scanner barcode received: ', data);
      console.log('barcode: ', data.barcode);
    },
    error => {
      console.log('## TecToy Scanner error: ', error);
    },
);
```
