# Car Gate Status

## Topic  
The topic name follows the rules:<br>
{env}/data/{application}/{context}/{data-type}
| Type | Topic Name |
|:----|:----|
| Real | real/data/cargate/{gateId}/status |
| Simulation | sim{simulationId}/data/cargate/{gateId}/status |


## Description  

&emsp;Use the following message format when you subscribe each car gate status.

## Cycle  

&emsp;Irregular. If any of the following values change, a message will be published.
* requestID
* applyType
* carNumberAuthResult
* carNumber
* dsrcAuthResult
* dsrc
* nfcAuthResult
* nfc
* authNGReason
* startUpSensor
* gateCloseSensor
* gateFullOpenSensor
* gateFullCloseSensor
* bikeSensor
* forcedGateOpen
* gateReleaseMode
* closedMode
* fullCountMode
* maintenanceMode
* dateTimeFrom
* dateTimeTo
* userType


## Published by  

| Type |  |
|:----|:----|
| Real | AMANO Web Server |
| Simulation | Digital Twin Platform |


## Message  

&emsp;Format: JSON  
&emsp;QoS: 1  
&emsp;retain: TRUE  
&emsp;Content: defined in [here](https://docs.google.com/document/d/18CnkV3d81cFSx0zaxoSIxRi0yVAr0jEj/edit).

## Sample  

```json
{
    "sendDateTime": "2023-10-20 13:59:30.623",
    "noticeData": {
        "noticeDateTime": "2023-10-20 13:59:30.095",
        "requestID": "123456789012345678901230",
        "applyType": 2,
        "gateID": 1,
        "inOutType": 1,
        "carNumberAuthResult": 1,
        "carNumber": {
            "plateRegion": "横浜",
            "plateCode": "３００",
            "plateSymbol": "あ",
            "plateLicense": "１２３４"
        },
        "dsrcAuthResult": 0,
        "dsrc": "0117D618E90F",
        "nfcAuthResult": 0,
        "nfc": "",
        "authNGReason": 0,
        "startUpSensor": true,
        "gateCloseSensor": false,
        "gateFullOpenSensor": false,
        "gateFullCloseSensor": false,
        "bikeSensor": false,
        "forcedGateOpen": false,
        "gateReleaseMode": false,
        "closedMode": false,
        "fullCountMode": false,
        "maintenanceMode": false,
        "dateTimeFrom": "2023-10-20 00:00",
        "dateTimeTo": "2023-10-20 23:59",
        "userType": 1
    }
}

```
