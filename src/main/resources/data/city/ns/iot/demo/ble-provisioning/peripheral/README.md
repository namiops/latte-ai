# Device-side Logics for Devices Mobile Enrollment with BLE

This codebase contains the device-side logics of a stand alone PoC that implements [Device Mobile Provisioning/Enrollment/Onboarding Through BLE (Bluetooth Low Energy)](https://docs.google.com/document/d/1zgjf97xIy6PXa3mTQzrHVHn6CWCEng6BCVCZ5S-ZX4I/edit#heading=h.bkazm7iqnrc5). It is created with Golang and [Tinygo](https://tinygo.org/).

Currently, it is designed to run on the Raspberry Pi 4 platform. This codebase relies on [Tinygo/Bluetooth](https://github.com/tinygo-org/bluetooth) to enable its BLE peripheral features. 

For a more in-depth explanation of the enrollment process and the underlying design principles, please refer to [related Technical Notes (TNs)](../README.md#related-tns).

> **⚠️ IMPORTANT NOTE:** Please note that Tinygo/Bluetooth currently supports [ONLY Linux](../README.md#rapberry-pi-4) machines as peripherals. Therefore, building targets using `Bazel` on OSs such as MacOS or Windows will be [skipped](./pkg/BUILD).

## How does it work?

### 1. Registering BLE Services

The device-side logic of the onboarding flow starts with the device registering its `service` and `characteristics`. This step is similiar to defining `handlers` when building an HTTP server in Golang.

### 2. Advertising and Connecting

After defining BLE services, the device advertises itself and awaits a BLE connection request from the MiniApp.

### 3. Listening and Serving

Once the BLE connection is established, the device launches a `goroutine` to listen for incoming `read` or `write` operations from the MiniApp. The device responds with corresponding actions based on the `resourceFlag` value in the operation, as defined in its `characteristics`. Here are some examples:

1. `read` + `resourceFlag == 1`: The MiniApp tries to read the CSR (Certificate Signing Request) used for provisioning. In response, the device prepares a CSR for the MiniApp to read.

2. `write` + `resourceFlag == 1`: The MiniApp tries to write IoTA client information (e.g., `groupName`, `IoTABrokerHost`), necessary for CSR generation, to the device.

3. `write` + `resourceFlag == 2`: The MiniApp tries to write certificates. Upon receiving certificates, the device becomes capable of sending logs to the [Observability Dashboard](https://observability.agora-lab.woven-planet.tech/grafana/d/1b36cec9470a9b87e07520281f7bb49c654f6de7/ble-demo-dashboard?orgId=1).

> **⚠️ IMPORTANT NOTE:** Sending device logs requires a sensor (e.g., Adafruit DHT 11 used in our case) connected to the Pi. You may need to adjust the device logging logic to align with your sensor configurations.

## Get Started!

Performing operations on the device side is straightforward. You can either [start](#starting-the-ble-server) the BLE server or [stop](#stopping-the-ble-server) it.

> **⚠️ IMPORTANT NOTE:** Ensure that the Pi is connected to `WOVEN-IOT` before proceeding. Failing to do so may result in issues with sending device logs.

### Starting the BLE Server

Navigate to the `cmd` directory and execute the following command:

```bash
go run .
```

It usually takes a few seconds for the Pi to complete BLE configuration and become discoverable.

### Stopping the BLE server

To stop the BLE server, use a keyboard interruption, such as` Ctrl + C`.

## Todos

- [ ] Add tests.
- [ ] Reuse existing pkgs.
 