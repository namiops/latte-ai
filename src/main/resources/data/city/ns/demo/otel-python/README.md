# Python OTEL sample

This sample shows how to use Python SDK to send OTEL signals to [telemetry-collector](https://developer.woven-city.toyota/docs/default/component/telemetry-collector/).

## Usage

Copy a url from the document above, set "TC_URL" variable and run the script.

## Examples

### Sending directly to telemetry-collector

```bash
TC_URL="https://iot.woven-city-api.toyota/telemetry" python3 otel.py
```

### Sending via [IoTAD](https://developer.woven-city.toyota/docs/default/component/iota-service/Tasks/iotad/)

```bash
TC_URL="http://127.0.0.1:4318" python3 otel.py
```