# MQTT Test Tools
This repository contains MQTT test tools written in Go, designed for publishing and subscribing to MQTT topics using mTLS (mutual TLS) communication.

## OverView

This project includes three main test tools. Each tool performs specific operations on MQTT topics.

| Test Tools | Description |
| ---- | ---- |
| pub_gate_state.go | Publishes a `car gate state` JSON file to a specified MQTT topic with a given `gate-id`. |
| pub_gate_scenario.go | Publishes a sequence of `car gate state` JSON files defined in a scenario file to a specified MQTT topic with a given `gate-id`. |
| sub_aggregated_gate_states.go | Subscribes to a specified MQTT topic for `aggregated car gate state` and outputs messages to the standard output. |

## Usage Steps

### Step 1: Get Certification Files

You need some certification files from 1Password. Here's how to get them:

1. Open 1Password.
2. Go to **TrafficSignal** > **[For PreProd]MQTT_IOTA_***.
3. Download these files:
  - `fss-ts-test00_ca.pem`
  - `fss-ts-test00_crt.pem`
  - `fss-ts-test00_key.pem`

### Step 2: Put Certification Files in `/certs/` Folder

After downloading, put these files in the `certs` folder inside `.local_debug`. Your folder should look like this:

```console
.local_debug
├── certs
│   ├── fss-ts-test00_ca.pem
│   ├── fss-ts-test00_crt.pem
│   └── fss-ts-test00_key.pem
```

### Step 3: Get Password(IOTA_SVC_PASSWORD) from 1Password

You also need a password from 1Password. Here’s how to get it:

1. Open 1Password.
2. Go to **[For PreProd]MQTT_IOTA_USERNAME_PASSWORD**.
3. Copy the password.

### Step 4: Set environment variables

You need to set following 8 environment variables.

```sh
export IOTA_BROKER_HOST=iot.agora-dev.w3n.io
export IOTA_BROKER_VHOST=traffic-signal-dev
export IOTA_SVC_USER=traffic-signal-dev_fss-ts_fss-ts-test00
export CONSUME_TOPIC_PREFIX=sim
export PUBLISH_TOPIC_PREFIX=epallete-sim
```

Replace `PASSWORD` with the password you copied from 1Password.

```sh
export IOTA_SVC_PASSWORD=PASSWORD
```

For the certificate paths, you need to specify the absolute path. You can use the following commands to set them correctly. Note the command inside the parentheses (`$(git rev-parse --show-toplevel)`). It automatically finds the top-level directory of your Git repository.

```sh
export TLS_CLIENT_CERT_PATH=$(git rev-parse --show-toplevel)/projects/backend-car-gate/.local_debug/certs/fss-ts-test00_crt.pem
export TLS_PRIVATE_KEY_PATH=$(git rev-parse --show-toplevel)/projects/backend-car-gate/.local_debug/certs/fss-ts-test00_key.pem
```

### Step 5: Run test tools

#### pub_gate_state.go

Publishes a JSON file for `car gate state` to a specified MQTT topic.<br>
The `state file` and `gate-id` are specified as arguments.

```bash
$ go run ./pub_gate_state.go ~/city/projects/backend-car-gate/tests/testdata/01_single_vehicle_epalette/0100_waiting.json 20
Message published to topic: real/data/cargate/20/status
Message content: {"sendDateTime":"2024-08-30 13:50:40.991","noticeData":{"noticeDateTime":"2024-08-30 13:50:40.941","requestID":"","applyType":0,"gateID":20,"inOutType":1,"carNumberAuthResult":0,"carNumber":{"plateRegion":"","plateCode":"","plateSymbol":"","plateLicense":""},"dsrcAuthResult":0,"dsrc":"","nfcAuthResult":0,"nfc":"","authNGReason":0,"startUpSensor":false,"gateCloseSensor":false,"gateFullOpenSensor":false,"gateFullCloseSensor":true,"bikeSensor":false,"forcedGateOpen":false,"gateReleaseMode":false,"closedMode":false,"fullCountMode":false,"maintenanceMode":false,"dateTimeFrom":"","dateTimeTo":"","userType":0}}
```

#### pub_gate_scenario.go

Publishes a sequence of `car gate state` JSON files defined in a scenario file to a specified MQTT topic with a given `gate-id`.<br>
The `scenario file` and `gate-id` are specified as arguments.

```bash
$ go run ./pub_gate_scenario.go ~/city/projects/backend-car-gate/tests/testdata/01_single_vehicle_epalette/scenario.json 30
Topic: real/data/cargate/30/status
Delay:     0[ms] Publish: 0100_waiting.json
Delay:  1000[ms] Publish: 0200_startUpSensor_true.json
Delay:  1000[ms] Publish: 0311_carNumberAuthResult_3.json
Delay:  1000[ms] Publish: 0312_dsrcAuthResult_1.json
Delay:   500[ms] Publish: 0320_gateFullCloseSensor_false.json
Delay:  2000[ms] Publish: 0410_gateFullOpenSensor_true.json
Delay:  2500[ms] Publish: 0510_gateCloseSensor_true.json
Delay:  1000[ms] Publish: 0610_startUpSensor_false.json
Delay:  1000[ms] Publish: 0710_gateCloseSensor_false.json
Delay:   100[ms] Publish: 0720_auth_clear.json
Delay:   400[ms] Publish: 0810_gateFullOpenSensor_false.json
Delay:  2000[ms] Publish: 0910_gateFullCloseSensor_true.json
```

#### sub_aggregated_gate_states.go

Subscribes to a specified MQTT topic for `aggregated car gate state` and outputs messages to the standard output.

```bash
$ go run ./cmd/sub_aggregated_gate_states/sub_aggregated_gate_states.go 
Received Time:2024-08-26 14:17:14.243 Topic: epallete-sim/data/cargate/aggregated/status
gateID:30 NoticeDateTime:2024-10-21T04:59:30.095Z gateMode: [] armState:OPEN carnumber:0 dsrc:0 nfc:0
gateID:31 NoticeDateTime:2024-10-22T04:59:30.095Z gateMode: [] armState:OPEN carnumber:0 dsrc:0 nfc:0
gateID:33 NoticeDateTime:2024-10-22T04:59:30.095Z gateMode: [] armState:OPEN carnumber:0 dsrc:0 nfc:0
gateID:36 NoticeDateTime:2024-10-25T04:59:30.095Z gateMode: [] armState:OPEN carnumber:0 dsrc:0 nfc:0
gateID:37 NoticeDateTime:2024-10-25T04:59:30.095Z gateMode: [ForcedGateOpen] armState:OPEN carnumber:0 dsrc:0 nfc:0
All Messages: &model.AggregatedCarGateState{MsgType:100008, MessageTimestamp:"2024-08-26T05:17:13.826Z", Gates:[]model.Gate{model.Gate{NoticeDateTime:"2024-10-22T04:59:30.095Z", GateID:31, InOutType:1, GateMode:model.GateMode{ForcedGateOpen:false, GateReleaseMode:false, ClosedMode:false, FullCountMode:false, MaintenanceMode:false}, ArmStatus:"OPEN", CarAuthInfo:model.CarAuthInfo{RequestID:"", UserType:0, ApplyType:0, DateTimeFrom:"", DateTimeTo:"", CarNumberAuthResult:0, CarNumber:model.CarNumber{PlateRegion:"", PlateCode:"", PlateSymbol:"", PlateLicense:""}, DsrcAuthResult:0, Dsrc:"", NFCAuthResult:0, NFC:"", AuthNGReason:0}}, model.Gate{NoticeDateTime:"2024-10-25T04:59:30.095Z", GateID:36, InOutType:1, GateMode:model.GateMode{ForcedGateOpen:false, GateReleaseMode:false, ClosedMode:false, FullCountMode:false, MaintenanceMode:false}, ArmStatus:"OPEN", CarAuthInfo:model.CarAuthInfo{RequestID:"", UserType:0, ApplyType:0, DateTimeFrom:"", DateTimeTo:"", CarNumberAuthResult:0, CarNumber:model.CarNumber{PlateRegion:"", PlateCode:"", PlateSymbol:"", PlateLicense:""}, DsrcAuthResult:0, Dsrc:"", NFCAuthResult:0, NFC:"", AuthNGReason:0}}, model.Gate{NoticeDateTime:"2024-10-25T04:59:30.095Z", GateID:37, InOutType:1, GateMode:model.GateMode{ForcedGateOpen:true, GateReleaseMode:false, ClosedMode:false, FullCountMode:false, MaintenanceMode:false}, ArmStatus:"OPEN", CarAuthInfo:model.CarAuthInfo{RequestID:"", UserType:0, ApplyType:0, DateTimeFrom:"", DateTimeTo:"", CarNumberAuthResult:0, CarNumber:model.CarNumber{PlateRegion:"", PlateCode:"", PlateSymbol:"", PlateLicense:""}, DsrcAuthResult:0, Dsrc:"", NFCAuthResult:0, NFC:"", AuthNGReason:0}}, model.Gate{NoticeDateTime:"2024-10-22T04:59:30.095Z", GateID:33, InOutType:1, GateMode:model.GateMode{ForcedGateOpen:false, GateReleaseMode:false, ClosedMode:false, FullCountMode:false, MaintenanceMode:false}, ArmStatus:"OPEN", CarAuthInfo:model.CarAuthInfo{RequestID:"", UserType:0, ApplyType:0, DateTimeFrom:"", DateTimeTo:"", CarNumberAuthResult:0, CarNumber:model.CarNumber{PlateRegion:"", PlateCode:"", PlateSymbol:"", PlateLicense:""}, DsrcAuthResult:0, Dsrc:"", NFCAuthResult:0, NFC:"", AuthNGReason:0}}, model.Gate{NoticeDateTime:"2024-10-21T04:59:30.095Z", GateID:30, InOutType:1, GateMode:model.GateMode{ForcedGateOpen:false, GateReleaseMode:false, ClosedMode:false, FullCountMode:false, MaintenanceMode:false}, ArmStatus:"OPEN", CarAuthInfo:model.CarAuthInfo{RequestID:"", UserType:0, ApplyType:0, DateTimeFrom:"", DateTimeTo:"", CarNumberAuthResult:0, CarNumber:model.CarNumber{PlateRegion:"", PlateCode:"", PlateSymbol:"", PlateLicense:""}, DsrcAuthResult:0, Dsrc:"", NFCAuthResult:0, NFC:"", AuthNGReason:0}}}}
```
