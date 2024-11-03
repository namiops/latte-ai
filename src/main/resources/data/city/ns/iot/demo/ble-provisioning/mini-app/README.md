# MiniApp-side Logics for Devices Mobile Enrollment with BLE

This codebase contains the MiniApp-side logics of a stand alone PoC that implements [Device Mobile Provisioning/Enrollment/Onboarding Through BLE (Bluetooth Low Energy)](https://docs.google.com/document/d/1zgjf97xIy6PXa3mTQzrHVHn6CWCEng6BCVCZ5S-ZX4I/edit#heading=h.bkazm7iqnrc5). It is created with [Dart](https://dart.dev/) and [Flutter](https://flutter.dev/).

## How does it work?

### Architecture

<img src="docs/assets/miniapp-arch.png" alt="drawing" width="500"/>

MiniApps are sub-applications that will be available inside the WovenApp. Diagram above shows the relationship between Mini Apps and WovenApp and how does a Mini App works. Please check [this](https://github.tri-ad.tech/pages/R-D-WCM/wig-miniapp-docs/) to learn details of it.

## Get started!

> **⚠️ IMPORTANT NOTE:** Please ensure that you onboard the device to the `lab` context. Failing to do so may result in encountering CORS (Cross-Origin Resource Sharing) issues during the provisioning process.

### Install Dart and Flutter

This mini app is built based on [Dart](https://dart.dev/) and [Flutter](https://flutter.dev/) technology, please be sure you have them installed before running it.

Here are some useful resources for you to learn about them:

- [Get the Dart SDK.](https://dart.dev/get-dart)

- [Lab: Write your first Flutter app.](https://docs.flutter.dev/get-started/codelab)

- [Cookbook: Useful Flutter samples.](https://docs.flutter.dev/cookbook)

### Check your network

Please ensure your PC and your company phone are under the same network before starting compliling the MiniApp.

> **⚠️ IMPORTANT NOTE:** Since your phone is unlikely to be able to connect to `WOVEN-CORP`, you MAY need to connect your PC to your phone's hotspot. Due to unknown reasons, conncecting both your phone and your PC to `WOVEN-GUEST` doesn't work.

### Build the MiniApp

1. Navigate to the root directory of the miniapp and run the following commands:

    ```bash
    dart pub get
    ```
    
    and:

    ```bash
    flutter pub get
    ```

    These commands will check MiniApp's `pubspec.yaml` file and will download the required dependencies for MiniApp, and generates the `.dart_tool` directory.

2. Run the command below to get the MiniApp up and running:

    ```bash
    flutter run -d web-server --web-port 8080 --web-hostname 0.0.0.0
    ```

    You will be able to open the webview of this MiniApp at your `localhost:8080` after compilation.

### Run the MiniApp inside your WovenApp

Since this MiniApp needs Native BLE APIs for all of its funtionalities, you need to run it inside your WovenApp's sandbox. Please follow the steps below:

1. [Start the BLE server of your device](../peripheral/README.md#starting-the-ble-server).

2. Turn on your company's phone's bluetooth.

3. Open the sandbox of your WovenApp and input your local URL along with the correct Port number into the textbox (ex. `http://192.168.0.59:8080`). Please check [here](https://github.tri-ad.tech/pages/R-D-WCM/wig-miniapp-docs/docs/miniapp-sdk/debugging/sandbox) for detailed steps. Try to use Mini App's [virtual console](https://github.tri-ad.tech/pages/R-D-WCM/wig-miniapp-docs/docs/miniapp-sdk/debugging/error-reporting) for efficient debugging.

4. Press the `Open Mini App` button.

5. A User login page will appear. Enter your `lab` `username` and `password` to log in. This step is required as your requests to IoTA likely require an `accessToken`.

6. A list of nearby BLE devices, including your device, will be displayed. If not, refresh by clicking the `refresh` icon in the top right corner.

7. Click on your device to establish a connection. This may take a second or two.

8. You will see a list of services, select the right one to naviagte.
   >  **NOTE**: A BLE device MAY feature multiple services, each with a unique `UUID`. You must select the one defined in the device's logics. If uncertain, refer to the [device's source code](../peripheral/pkg) for confirmation. This design is suboptimal and should be improved in the future.

9. You will see a `Onboard Device` button. Click to onboard your device. This might take a few second to tens of seconds, depending on the device's capbility to generate CSR. During the process, you will observe the interactions between the MiniApp and the device inside the device's terminal.

    > **⚠️ IMPORTANT NOTE:** The BLE connection is unstable from time to time. Therefore, the unboarding process could stop halfway unexpectly. If it happens, please re-click the `Onboard Device` button or restart the BLE server and the MiniApp.

10. Upon device onboarding is complete, you will see the device sending logs to the [Observability Dashboard](https://observability.agora-lab.woven-planet.tech/grafana/d/1b36cec9470a9b87e07520281f7bb49c654f6de7/ble-demo-dashboard?orgId=1) and also outputting logs to the terminal. Device's private key and certificates will be stored in its `~/.iotapoc/` directory by default.


## Todos

- [ ] Unit test

## Reference

- https://github.tri-ad.tech/marouane-boumeziane/iot-device-enrollment-mini-app-poc

## Appendix

### Overall architecture (D2)

```
mobile: Mobile Phone (iOS/Android) {
  wa: Woven App {
    webview: WebView {
      miniapp: Mini App {
        style: {
          stroke: orange
          font-color: orange
        }
        feat1: Inventor-defined\n features {
          style: {
            stroke: purple
            font-color: purple
            fill: "#e1d5e7"
          }
        }
        feat2: External\n SDK
        feat1 <-> feat2
      }
    }
    webview.style.stroke-dash: 5
    internal: Woven App Internal SDK {
      style: {
        stroke: purple
        font-color: purple
        fill: "#e1d5e7"
      }
    }
    webview.miniapp.feat2 <-> internal
    native: Native APIs \n (ex. BLE) {
      style: {
        stroke: blue
        font-color: blue
        fill: "#e1d5e7"
      }
    }
    city: City APIs \n (ex. Account) {
      style: {
        stroke: orange
        font-color: orange
        fill: "#e1d5e7"
      }
    }
    internal <-> native
    internal <-> city
  }
  wa.native <-> os
  os: OS/HW
}

agora: City Platform {
  idp: Identity Provider
  other: Other Services
}

# user <-> mobile.wa.webview.miniapp.feat1
mobile.wa.city <-> agora.idp
mobile.wa.city <-> agora.other

```