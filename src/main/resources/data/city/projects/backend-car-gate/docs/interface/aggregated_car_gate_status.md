# Aggregated Car Gate Status

## Topic  
The topic name follows the rules:<br>
{env}/data/{application}/{context}/{data-type}
| Type | Topic Name |
|:----|:----|
| Real | real/data/cargate/aggregated/status |
| Simulation | sim{simulationId}/data/cargate/aggregated/status |



## Description  

&emsp;Use the following message format when you subscribe aggregated car gate status.

## Cycle  

&emsp;2Hz

## Published by  

&emsp;BCG (Backend of Car Gate)


## Message  
&emsp;Format: JSON (serialized in binary format by MessagePack)  
&emsp;QoS: 0  
&emsp;retain: FALSE  

| key | type | description |
| -- | -- | -- |
| msg_type          | int    | fixed parameter 100008 (TBD) |
| messageTimestamp  | string | ISO8601 format ( including milliseconds )|
| gates             | array<[gate](#gate)>  | arrays of gates|

### gate
Information for each gate.
| key | type | description |
|:----|:----|:---|
| noticeDateTime | string  | Date and time when the status of the each gate changed. |
| gateID         | integer | ID of car gate.|
| inOutType      | integer | Entry/Exit Category:<br>1: Entry<br>2: Exit |
| gateMode       | [gateMode](#gatemode)       | The current mode of the gate.|
| armStatus      | string  | Status of the gate arm.<br>It can be one of the following values: "OPEN", "MOVE", "CLOSE". |
| carAuthInfo    | [carAuthInfo](#carauthinfo) | Authentication and authorization information for a car.|

#### gateMode
The current mode of the gate.
| key | type |  description |
|:----|:----|:---|
| forcedGateOpen  | boolean | A command to open the gate to allow a single vehicle to pass through.<br> false: OFF<br> true: ON (one-shot trigger ※1)|
| gateReleaseMode | boolean | A mode to keep the gate continuously open.<br>false: OFF<br> true: ON (continuously open)|
| closedMode      | boolean | A mode to indicate whether the business is closed or operational.<br> false: OFF (operational)<br> true: ON (closed) |
| fullCountMode   | boolean | A mode to indicate whether the parking lot is full or vacant.<br>false: OFF (vacant)<br> true: ON (full)|
| maintenanceMode | boolean | A mode for maintenance.<br>false: OFF<br> true: ON (maintenanceMode) |

※1: It momentarily changes to `true` to open the gate and then immediately reverts to `false`.

#### carAuthInfo
Authentication and authorization information for a car.
| key | type | description |
|:----|:----|:---|
| requestID      | string  | Half-width alphanumeric 24-character strings.<br>This is a number assigned by the Car Gate Management System and is a unique code within the system.<br> If auth result isn't OK, this will be an empty string. |
| userType       | integer | User Type: (Up to 15 types)<br>Authentication OK: 1 to 15<br>Authentication NG: 0<br>As of 2024/1/18, the following are expected:<br>1: Resident<br>2: Visitor<br>3: Employee 1<br>4: Employee 2<br>5: Pickup/Drop-off<br>6: Logistics<br>7: e-Palette, Share Car<br>8: Moving (e.g. YAMATO transport)<br>9: VIP<br>10: Operational, Master |
| applyType      | integer |  Application Type:<br>1: Indefinite (For long-term application car)<br>2: Specified period (For short-term application car)<br>If auth result isn't OK, this will be 0. |
| dateTimeFrom   | string  | Only for cars with short-term applications. Leave empty string for cars with long-term applications.<br>Auth OK: Start Date and Time of Use.(ISO8601 format)<br>Auth NG: empty string |
| dateTimeTo     | string  | Only for cars with short-term applications. Leave empty string for cars with long-term applications.<br>Auth OK: End Date and Time of Use.(ISO8601 format)<br>Auth NG: empty string |
| carNumberAuthResult | integer       | Car number auth result:<br>0: Not processed (when auth is not completed)<br>1: Auth OK<br>2: Auth NG<br>3: No car number. (when there is no license plate and it transitions to DSRC without inquiry)<br>  |
| carNumber       | [carNumber](#carNumber) | Information of car number(license) plate. |
| dsrcAuthResult      | integer       | DSRC auth result:<br>0: Not processed (when auth is not completed)<br>1: Auth OK<br>2: Auth NG<br>3: No on-board DSRC unit. (when there is no DSRC and it transitions to NFC without inquiry) |
| dsrc                | string        | 12-character strings for DSRC (Wireless Call Number).<br> If it does not exist, an empty string will be used. |
| nfcAuthResult       | integer       | NFC auth result:<br>0: Not processed<br>1: Auth OK<br>2: Auth NG |
| nfc                 | string        | 16-character strings for NFC (FeliCa IDm)<br>If it does not exist, an empty string will be used. |
| authNGReason        | integer       | Reason for auth NG:<br>0: Not auth NG<br>1: Not registered<br>2: User type not allowed to pass (registered)<br>3: Outside of permissible usage time range (registered)<br>99: Other<br>|

##### carNumber
| key | type |description |
|:----|:----|:---|
| plateRegion  | string | Full-width 4-character strings such as "横浜".<br>If it does not exist, an empty string will be used. |
| plateCode    | string | Full-width 3-character strings such as "３００" and "３０A".<br>If it does not exist, an empty string will be used. |
| plateSymbol  | string | Full-width 1-character string(kana characters) such as "あ".<br>If it does not exist, an empty string will be used. |
| plateLicense | string | Full-width 4-character strings such as "１２３４".<br>If it does not exist, an empty string will be used. |

## Sample  

```json
{
    "msg_type": 100008,
    "messageTimeStamp": "2023-07-31T16:51:20.150+09:00",
    "gates": [
        {
            "noticeDateTime": "2023-07-31T16:51:19.850+09:00",
            "gateID": 1,
            "inOutType": 1,
            "gateMode": {
                "forcedGateOpen": false,
                "gateReleaseMode": false,
                "closedMode": false,
                "fullCountMode": false,
                "maintenanceMode": false
            },
            "armStatus": "OPEN",
            "carAuthInfo": {
                "requestID": "123456789012345678901230",
                "userType": 7,
                "applyType": 1,
                "dateTimeFrom": "",
                "dateTimeTo": "",
                "carNumberAuthResult": 0,
                "carNumber": {
                    "plateRegion": "",
                    "plateCode": "",
                    "plateSymbol": "",
                    "plateLicense": ""
                },
                "dsrcAuthResult": 1,
                "dsrc": "0117D618E90F",
                "nfcAuthResult": 0,
                "nfc": "",
                "authNGReason": 0
            }
        },
        {
            "noticeDateTime": "2023-07-31T16:51:19.050+09:00",
            "gateID": 3,
            "inOutType": 1,
            "gateMode": {
                "forcedGateOpen": false,
                "gateReleaseMode": false,
                "closedMode": false,
                "fullCountMode": false,
                "maintenanceMode": false
            },
            "armStatus": "MOVE",
            "carAuthInfo": {
                "requestID": "123456789012345678901231",
                "userType": 2,
                "applyType": 2,
                "dateTimeFrom": "2023-10-01T00:00:00+09:00",
                "dateTimeTo": "2023-10-01T02:00:00+09:00",
                "carNumberAuthResult": 1,
                "carNumber": {
                    "plateRegion": "横浜",
                    "plateCode": "３００",
                    "plateSymbol": "あ",
                    "plateLicense": "１２３４"
                },
                "dsrcAuthResult": 0,
                "dsrc": "",
                "nfcAuthResult": 0,
                "nfc": "",
                "authNGReason": 0
            }
        },
        {
            "noticeDateTime": "2023-07-31T16:40:00.010+09:00",
            "gateID": 2,
            "inOutType": 1,
            "gateMode": {
                "forcedGateOpen": false,
                "gateReleaseMode": false,
                "closedMode": false,
                "fullCountMode": false,
                "maintenanceMode": false
            },
            "armStatus": "CLOSE",
            "carAuthInfo": {
                "requestID": "",
                "userType": 0,
                "applyType": 0,
                "dateTimeFrom": "",
                "dateTimeTo": "",
                "carNumberAuthResult": 0,
                "carNumber": {
                    "plateRegion": "",
                    "plateCode": "",
                    "plateSymbol": "",
                    "plateLicense": ""
                },
                "dsrcAuthResult": 0,
                "dsrc": "",
                "nfcAuthResult": 0,
                "nfc": "",
                "authNGReason": 0
            }
        }
    ]
}

```
