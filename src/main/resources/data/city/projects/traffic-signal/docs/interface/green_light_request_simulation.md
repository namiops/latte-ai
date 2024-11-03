# Green Light Request (Simulation)

## Topic

&emsp;dtp/intersection/{intersection ID}/green

## Description  

&emsp;Use the following message format when you subscribe Green Light Request (simulation).

## Cycle  

&emsp;Basically every few minutes.

## Published by  

&emsp;Back-end of Traffic Signal (BTS)

## Message  

&emsp;Format: JSON  
&emsp;QoS: 0  

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
