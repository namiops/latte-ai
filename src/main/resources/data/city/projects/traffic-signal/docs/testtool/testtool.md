# Test Tools Documentation of Traffic Signal System, FSS, WCM

* This document provides instructions on how to use the various test tools included in this repository.

## Test Tools Overview
The repository includes the following test tools:

| No| Test Tool   | What you can do        |
|---|-------------|-----------------------------------|
|1| **convLL2WovenXY** | You can convert latitude and longitude coordinates in [route_info](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/route_info.md) to woven XY coordinates |
|2| **greenLightPubTool** | You can publish [change(extend) green Request](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/green_light_request_real.md).<br>(Publish only once to iotcoreBts.) |
|3| **intersectionPubTool** | You can publish traffic light status as [KeiB](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/traffic_light_status_real.md).<br>(Publish 2Hz to iotcoreBts) |
|4| **vehicleApproachPubTool** | You can publish moving [vehicle location info](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/vehicle_location.md).<br>(Publish 10Hz to iotcoreAdk or iota) |
|5| **vehicleApproachWithRouterPubTool** | You can publish moving [vehicle location info](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/vehicle_location.md) in 10Hz with [route info](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/route_info.md) in 1/3 Hz.<br>(Publish 10Hz and 1/3Hz to iotcoreAdk)|
|6| **vehiclePubTool** | You can publish [vehicle location info](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/interface/vehicle_location.md) that is edited as you like.<br>(Publish only once to iotcoreAdk)   |

## Usage

***Note:*** 
* Please remember to set the [environment variables](https://github.com/wp-wcm/city/tree/main/projects/traffic-signal#export-environment-variables) before running test tools.<br>
* Please remember to download certs files of MQTT Brokers (iotcoreBts/iotcoreAdk/iota) from 1PassWord for TrafficSignal Project.

### 1. convLL2WovenXY

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/convLL2WovenXY/convLL2WovenXY.go
0,83.6952764100497,42.73839831992518    // index of route_to_destination, WovenX, WovenY
1,87.58329132118524,41.305524614304886
2,92.73550441252155,38.55566876605735
3,97.88771760992677,35.80581275765144
4,100.73201023660658,33.65266745175177
5,104.5143715762315,31.659764771233313
6,107.76396337783808,29.889193496332155
```
* You can edit the route_to_destination in route_info.json.

```json
// ./test/cmd/convLL2WovenXY/route_info.json
{
    "msg_type": 100006,
    "timestamp": 0.0,
    "route_to_destination": [
        [
            35.21834600597392, // latitude
            138.91344227498618 // longitude
        ],
        [
            35.21833294394389,
            138.91348491695308
        ],
        // omitted..
    ]
}

```

### 2. greenLightPubTool

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/greenLightPubTool/greenLightPubTool.go
What is request type GR1, GR2?: GR1
What is intersectionID: 1
```
* MQTT Broker Host
  * iotcoreBts：aft8p97py49u8-ats.iot.ap-northeast-1.amazonaws.com:8883
* TopicName
  * fromBts/{intersectionID}
* Prameters
  * request type
    * GR1 means change green request.
    * GR2 means extend green request.
  * intersectionID
    * The intersectionID for B12 is 1.


### 3. intersectionPubTool

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/intersectionPubTool/intersectionPubTool.go
You can select a signal step to publish.
The signal step is described in following doc.
https://docs.google.com/spreadsheets/d/1bHQRH5LbLvE30HmUJA6gKXt4neLemzpE
1A
1B
2
3
4A
4B
4C
5
6
Which signal step do you publish? Type 1A ~ 6?: 1A
Publishing traffic signal status in 2Hz......
```
* MQTT Broker Host
  * iotcoreBts：aft8p97py49u8-ats.iot.ap-northeast-1.amazonaws.com:8883
* TopicName
  * fromTs/intersection{intersectionID}
* Prameters
  * signal step
    * The signal step is described in this [doc](https://docs.google.com/spreadsheets/d/1bHQRH5LbLvE30HmUJA6gKXt4neLemzpE).

### 4. vehicleApproachPubTool

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/vehicleApproachPubTool/vehicleApproachPubTool.go
A. IoTCore(Epalette)
B. IoTA(GuideMobi)
Which Broker do you want to publish from? A or B ?: A
A. speed=12.5m/s(B12)
B. speed=5m/s(B12)
C. speed=0.5m/s(B12)
D. speed=4.72m/s(B12)
E. speed=3.47m/s(B12)
F. speed=2.78m/s(B12)
G. speed=4.72m/s(9Block)
H. speed=3.47m/s(9Block)
I. speed=2.78m/s(9Block)
What is request type A~I?: A
```
* MQTT Broker Host
  * iotcoreAdk(ePalette)：a2o2dh20a7ylnn-ats.iot.ap-northeast-1.amazonaws.com:8883
  * iota(GuideMobi)：iot.cityos-dev.woven-planet.tech:8883
* TopicName
  * iotcoreAdk(Epalette)：VWC01/data/vehicle/1/location
  * iota(GuideMobi)：GM01/data/vehicle/1/location


### 5. vehicleApproachWithRoutePubTool

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/vehicleApproachWithRoutePubTool/vehicleApproachPubToolwithRoute.go
```
* MQTT Broker Host
  * iotcoreAdk(Epalette)：a2o2dh20a7ylnn-ats.iot.ap-northeast-1.amazonaws.com:8883
* TopicName
  * VWC01/data/vehicle/1/location
  * VWC01/data/vehicle/1/route_info

### 6. vehiclePubTool

```sh
$ cd ./projects/traffic-signal/internal
$ go run ./test/cmd/vehiclePubTool/vehiclePubTool.go
```
* You can edit location_info in location_info.json
```json
// ./test/cmd/vehiclePubTool/location_info.json
{
  "msg_type": 400001,
  "timestamp": 1701067413.380785,
  "latitude": 35.22624233410198,
  "longitude": 138.89713612814532,
  "direction": 210.39111328125,
  "altitude": 0,
  "crs_type": 1,
  "position": [
    149.8726339,
    150.9831759,
    360.76797407865524
  ],
  "roll": 0,
  "pitch": 0,
  "yaw": -2.1012210845947266,
  "speed": 20,
  "acceleration": 0
}
```
* MQTT Broker Host
  * iotcoreAdk(Epalette)：a2o2dh20a7ylnn-ats.iot.ap-northeast-1.amazonaws.com:8883
* TopicName
  * VWC01/data/vehicle/1/location