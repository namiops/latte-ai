# Traffic Light Status (Simulation)

## Topic  

&emsp;fromDtp/intersection

## Description  

&emsp;Use the following message format when you subscribe Traffic Light Status (simulation).

## Cycle  

&emsp;2Hz

## Published by  

&emsp;Digital Twin Platform

## Message  

&emsp;Format: JSON  
&emsp;QoS: 0  
| key | type | unit | description |
|:----|:----|:----|:---|
| id | string |  | ID of intersection. |
| timestamp | string|  | ISO8601 format ( including milliseconds ) |
| status | string|  |  "available" or "unavailable" |
| trafficLightGroups[] | |  |  Array of traffic light|
| trafficLightGroups[].id | string |  | ID of traffic light. |
| trafficLightGroups[].color | string|  | Color of the circular traffic light. |
| trafficLightGroups[].arrows | string|  | Direction of the illuminated green arrow light. e.g. "backLeftDiagonal"|

## Sample  

```sh
{
    "id": "11",
    "timestamp": "2023-07-31T16:51:20.150+09:00",
    "status": "available",
    "trafficLightGroups": [
        {
            "id": "111",
            "color": "red",
            "arrows": [
                "backLeftDiagonal"
            ]
        },
        {
            "id": "112",
            "color": "green",
            "arrows": [
                "backLeftDiagonal"
            ]
        }
    ]
}
```
