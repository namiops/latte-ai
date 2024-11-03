# Devices Mobile Enrollment with BLE

This codebase contains a stand alone PoC that implements [Device Mobile Provisioning/Enrollment/Onboarding Through BLE (Bluetooth Low Energy)](https://docs.google.com/document/d/1zgjf97xIy6PXa3mTQzrHVHn6CWCEng6BCVCZ5S-ZX4I/edit#heading=h.bkazm7iqnrc5). It is created with Golang, [Tinygo](https://tinygo.org/), [Dart](https://dart.dev/), and [Flutter](https://flutter.dev/) technologies.

For a more in-depth explanation of the enrollment process and the underlying design principles, please refer to [related Technical Notes (TNs)](#related-tns).

## About the flow

### Architecture

<img src="docs/assets/overall-arch.png" alt="drawing" width="300"/>

The diagram above illustrates the overall architecture and interrelationships among components in the enrollment flow. In alignment with this diagram, consider the following typical scenario: As a trusted city resident (i.e. a resident with a keycloak account), I want to onboard one of my devices to Agora and claim my ownership over it. In order to do this, I open my Woven App and access the onboarding Mini App (Given my status as a city resident, it's very possible that I already have the Woven App installed on my phone). Then, I establish a BLE connection between my mobile phone and the target device. Upon pressing the "Onboard Device" button within the Mini App interface, the provisioning of my device takes place seamlessly.

### Sequence diagram

<img src="docs/assets/sequence-diagram.png" alt="drawing" width="550"/>

The sequence diagram above illustrates a successful enrollment flow. The flow starts when a city resident taps to log in to the Mini App and pairs their mobile phone with an IoT device (tentatively a Raspberry Pi 4), and ends with the successful onboarding of the device, which then initiates the transmission of device logs in the [OpenTelemetry (OTEL)](https://opentelemetry.io/) format to [IoTA](https://developer.woven-city.toyota/docs/default/Component/iota-service) with MQTT protocol.

## How to run it?

### Preparations

To get the flow up and runing, You need to make the following preparations:

1. [Rapberry Pi 4](#rapberry-pi-4).

2. [Woven App](#woven-app).

3. [`lab` Account ](#lab-account).

#### Rapberry Pi 4

In our PoC for enrollment, we use a [Raspberry Pi 4 (Pi)](https://www.raspberrypi.com/products/raspberry-pi-4-model-b/) as our sample IoT device for provisioning. Ideally, the device could be any unit running **Linux**. We use this Pi as our sample device because of its pins that connect to an [Adafruit DHT11](https://www.adafruit.com/product/386), a temperature and humidity sensor. Therefore, our Pi can send device logs to IoTA once the provisioning process is complete.

> **⚠️ IMPORTANT NOTE:** MacOS and Windows are NOT suitable as a device in our scenario. This is because the BLE library we used, [Tinygo/bluetooth](https://pkg.go.dev/tinygo.org/x/bluetooth), currently [lacks support for OSs other than Linux](https://github.com/tinygo-org/bluetooth/blob/release/README.md?plain=1#L93) when functioning as a BLE peripheral.

#### Woven App

The PoC Mini App operates within the sandbox of the Woven App (WA). Therefore, it is a MUST to have WA installed on your company iPhone before running the Mini App. You may have questions up until now, such as:

- **What is WA?** WA is the gate app of Woven City and creates touch points between inventor and residence/inventor.
- **What is a Mini App?** A [Mini App](./mini-app/README.md#architecture) is a sub-application that will be available inside the WA to allow inventors to develop their own customized services that will interact with WA and/or other city residents.

You can find more information about WA and Mini Apps in the pinned information section of this Slack channel: # wcm-wig-mini-app-ama. Please also reach out to this channel for the installation of WA.

#### `lab` Account
You MUST have a `lab` account to perform multiple requests to IoTA endpoints that requires a `JWT`.

> **⚠️ IMPORTANT NOTE:** Currently, `lab` is the only context that works for our flow. Using `lab2` or `dev` may let the Mini App encounter CORS (Cross-Origin Resource Sharing) issues during the provisioning process.

### Next steps

After getting the preparations ready, you will be able to run the Mini App and Pi to test the enrollment flow. Please check [Mini App-side's README](./mini-app/README.md) and [Device-side's README](./peripheral/README.md) for the detailed instructions.

## Userful resources

### Related TNs

- [TN-0186 IoT Devices Mobile Enrollment](https://docs.google.com/document/d/1PLRkpk9HON1hgGGjHTI4TFImD0qErCHKtEKnkT9Kyr4/edit#heading=h.lg61s7dek3ke) provides high-level design ideas and considerations on devices mobile enrollment flow.

- [TN-0371 Device Mobile Provisioning Through BLE (WIP)](https://docs.google.com/document/d/1zgjf97xIy6PXa3mTQzrHVHn6CWCEng6BCVCZ5S-ZX4I/edit#heading=h.bkazm7iqnrc5) focuses on detailed and practical design discussions, delving into the specifics and tradeoffs involved in implementing the BLE protocol for devices enrollment.

### Related Auth flows' docs

- [RFC 7636: Proof Key for Code Exchange](https://oauth.net/2/pkce/).

- [RFC 8628: OAuth 2.0 Device Authorization Grant](https://oauth.net/2/device-flow/)

## Appendix

### Overall architecture (D2)

```
mobile: Mobile Phone {
  wa: Woven App {
    miniapp: Mini App {
      style: {
        stroke: orange
        font-color: orange
      }
    }
    internal: Woven App Internal {
      style: {
        stroke: purple
        font-color: purple
        fill: "#e1d5e7"
      }
    }
    native: Native APIs \n (ex. BLE) {
      style: {
        stroke: blue
        font-color: blue
        fill: "#e1d5e7"
      }
    }
    miniapp <-> internal
    internal <-> native
  }
  wa.native <-> os
  os: OS/HW
}

device: IoT Device {
  shape: image
  icon: https://icons.terrastruct.com/essentials%2F226-alarm%20clock.svg
}

agora: Agora Platform {
  iota: IoTA {
    style: {
      stroke: purple
      font-color: purple
      fill: "#e1d5e7"
    }
  }
  keycloak: Keycloak {
    style: {
      stroke: green
      font-color: green
    }
  }
}

user: City Resident {
  shape: image
  icon: https://icons.terrastruct.com/aws%2F_General%2FUser_light-bg.svg
}

mobile.wa.miniapp <-> device: BLE pairing and \n data exchange {style: {font-size: 24; font-color: black}}
mobile.wa.internal <-> agora.keycloak: Auth {style: {font-size: 24; font-color: black}}
device <-> agora.iota

agora.iota <-> agora.keycloak: Introspect token {style: {font-size: 24; font-color: black}}

user -> mobile.wa: Login {style: {font-size: 24; font-color: black}}
user -> device: claim ownership {style: {font-size: 24; font-color: black}}

```

### Sequence diagram (PUML)

```
@startuml
' !theme mars
autonumber
group Preparation
    Device -> Device: advertise itself
    group User Login
        User -> MiniApp: input credentials \n and press login
        MiniApp -> Keycloak: user login
        Keycloak -> MiniApp: JWT
        MiniApp -> User: success
    end
    User -> MiniApp: press device name\n to connect
    MiniApp <-> Device: BLE pair
end

group Enrollment
  User -> MiniApp: press "onboard device" button
  loop BLE handshakes
    MiniApp -> Device: request for CSR with\n IoTA broker info
  end
  Device -> Device: create private key and CSR
  loop BLE handshakes
    Device -> MiniApp: send CSR
  end
  MiniApp -> WovenApp: extract Woven ID
  WovenApp -> MiniApp: return Woven ID
  MiniApp -> IoTA: create group
  IoTA -> MiniApp: statusCode
  alt statusCode == 201
    MiniApp -> WovenApp: store provKey
  else statusCode == 409
    MiniApp -> WovenApp: get provKey
    WovenApp ->  MiniApp: return provKey
  end
  MiniApp -> IoTA: add device
  WovenApp -> MiniApp: success
  MiniApp -> IoTA: provision device with CSR + provKey
  IoTA -> MiniApp: certificates 
  loop BLE handshakes
    MiniApp -> Device: certificates
  end
end
group Device logging
Device -> IoTA: telemetry
end
@endumldoc
```

