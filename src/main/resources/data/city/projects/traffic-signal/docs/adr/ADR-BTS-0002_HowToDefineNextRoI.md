# ADR-BTS-0002 Where and How to define next Region of Interest(RoI)

| Status | Last Updated |
|---|---|
|Drafted| 2023-10-30 |

## Context and Problem Statement

### Context

- Backend of Traffic Signal (we call BTS) must optimize the timing of the request to turn the traffic light's color to green.  
To achieve this, the BTS must know the time required for a vehicle to cross an intersection from vehicle's current location.
- We define the RoIGroup that means the route the vehicle will drive to cross the intersection.  
RoIGroup consists of a combination of rectangular RoI blocks.  
  - In order to calculate the distance, the RoIGroup should have the order of RoIs that the vehicle passes through.  
  
### Problem

- To design RoI and RoIGroup(having the order of RoIs), we need to consider the complicated cases below.
  1. Multiple RoIs connect to the same RoI.  
  For Example: Two roads merge into one.(Both RoI② and RoI③ connect to RoI④.)  
![The image of RoI.](https://github.tri-ad.tech/storage/user/8096/files/a2c20c47-1b9c-49b1-ae80-e825d26bbc73)

  1. The one RoI connect the multiple RoIs.  
  For Example: One road branches off into two.(RoI⑤ connect to both RoI⑥ and RoI⑦.)  
![The image of two roads merge into one.](https://github.tri-ad.tech/storage/user/8096/files/2454b193-afdb-4423-9bf2-15efd422362a)

## Considered Options

- Where and How should we define the information of the next RoI ?  
The Options are listed below.

|Option #| Problem 1 | Problem 2|Simplicity of codes| Where | How | Description|For Example <br>about Problem 2|
|:---:|:---:|:---:|:---:|:---:|:---:|:---|:---|
|1|Good|Good| <span style="color: red; ">Bad<br>※1</span>|struct RoI| As the field of the struct "nextRoI[]"  |define one  RoI for all next RoI.<br>(go through from RoI to RoI.nextRegion)<br>|Road5-6 : RoI5.nextRoI[0]<br>Road5-7 : RoI5.nextRoI[1]|
|2|Good|Good| <span style="color: red; ">Neutral<br>※2</span>|struct RoI| As the field of the struct "nextRoI"  |define one  RoI for one next RoI.<br>(pass through from RoI to RoI.nextRegion)|Road5-6 : RoI56.nextRoI<br>Road5-7 : RoI57.nextRoI|
|3|Good|Good|Good|struct RoIGroup|As the index of Array of RoI |define one RoIGroup for each individual route.<br> (pass through from *RoIGroup.RoI[0]* to *RoIGroup.RoI[1]*)|Road5-6 : RoIGroup56<br>Road5-7 : RoIGroup578|

- <span style="color: red; ">※1 The difference in the # of nextRoI per RoI.</span>  
- <span style="color: red; ">※2 The difference in the # of RoI per Region.</span>  

## Decision Outcome

- We change the design to the option#3 in the perspective of Simplicity of codes.

## Note

- 2023-10-30 : Drafted, Originator: Yuichi Takahashi