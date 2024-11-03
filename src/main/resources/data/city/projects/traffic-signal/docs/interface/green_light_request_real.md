# Green Light Request (Real)

## Topic

&emsp;fromBts/intersection{intersection ID}

## Description  

&emsp;Use the following message format when you subscribe Green Light Request (real).

## Cycle  

&emsp;Irregular. When BTS determines change or extension of traffic light status is needed to let vehicles pass an intersection without stop.

## Published by  

&emsp;Back-end of Traffic Signal (BTS)

## Message  

&emsp;Format: JSON  
&emsp;QoS: 1  


| key | type | unit | description |
|:----|:----|:----|:---|
| messageId | string |  | The prefix is set to "GR1-" or "GR2-". "GR1-" is for a green light request and "GR2-" is for a green light extension request. |
| timestamp | string|  | ISO8601 format ( including milliseconds ) |
| approachId | string|  |  the approachId, which BTS request to change or extend green light |

## Sample  

```sh
{
    "messageId": "GR1-c28f8363-36b9-4a4b-afff-ed05c46be438",
    "timestamp": "2023-11-30T11:02:36.692+09:00",
    "approachId": "1"
}
```
