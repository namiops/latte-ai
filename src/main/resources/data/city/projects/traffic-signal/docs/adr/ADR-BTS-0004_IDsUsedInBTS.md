# ADR-BTS- IDs used in BTS

| Status | Last Updated |
|---|---|
|Drafted| 2023-11-01 |

## Context and Problem Statement

### Context

- Backend of Traffic Signal(we call BTS) has two important roles in the Woven City Phase 1.  
    1. Publish the "Traffic Schedule Information based on [Kei-B(警b)](https://drive.google.com/file/d/13o8RvgGZpGArjHAclBdeWPiowkh2UyMu/view)" to the vehicles.  
    2. Request to turn the traffic light's color to green if vehicles approaches a Traffic Signal.
- To achieve the roles, it is necessary to use and define some kinds of IDs as listed below.
- We have both simulation and actual systems.
  - The simulation system consists of "DTP(Digital Twin Platform)" and "BTS" and "ADK(Autonomous Driving Kit)".  
    - In the simulation system, we have been developed  
    not using "TrafficLightApproachID" but using "TrafficLightGroupID".  
    That's because, "TrafficLightGroupID" is simpler than "TrafficLightApproachID"  
    and "TrafficLightGroupID" is enough to achieve our roles.
  - The actual system consists of "Traffic Signal" and  "BTS" and "ADK".  
    - In the actual system, we need to develop  
    not using "TrafficLightGroupID" but using "TrafficLightApproachID".  
    That's because, the actual system doesn't have "TrafficLightGroupID".

![The image of IDs used in our system.](https://github.tri-ad.tech/storage/user/8096/files/44ef9c1d-d412-483d-b168-67ef4769e2c3)

![The image of IDs used in our system.](https://github.tri-ad.tech/storage/user/8096/files/ca275de0-e751-4fdf-9da9-091291c5e687)

The spreadsheet file : [Link](https://docs.google.com/spreadsheets/d/1PSQXQYZn3E1VzsJt5Sfawho_0jZko1qd523Yu0qIhho/edit#gid=0)  
The slide file : [Link](https://docs.google.com/presentation/d/10SJJ9yvSV7tYftdRqMz5MZKWA7wrinwmNrrusCMW7zU/edit#slide=id.g29549ee1d89_0_0)

### Problem

- We want to develop the actual system as a copy of the simulation system, but...
  - In the case of developing the actual system using "TrafficLightGroupID", it will bring unnecessary complexity into the actual system.  
  That's because, we need to add convert logic from "TrafficLightApproachID" to "TrafficLightGroupID".
  - In the case of developing the actual system not using "TrafficLightGroupID", additional development effort for BTS/ADK required.  
  That's because, the simulation system we want to copy was developed based on "TrafficLightGroupID".

## Considered Options

- Under consideration ([Link](https://docs.google.com/presentation/d/1V37TkpouhkQQYdDVPCmFn-KP0fRVXfesF_RotTsV_lY/edit#slide=id.g295b88f84a2_0_0))

## Decision Outcome

- TBD

## Note

- 2023-11-01 : Drafted, Originator: Yuichi Takahashi