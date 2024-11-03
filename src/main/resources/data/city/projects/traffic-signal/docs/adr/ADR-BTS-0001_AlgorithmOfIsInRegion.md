# ADR-BTS-0001 Algorithm Of "IsInRegion"

| Status | Last Updated |
|---|---|
|Approved| 2023-11-01 |

## Context and Problem Statement

### Context

- Backend of Traffic Signal must detect vehicles approaching a Traffic Signal in order to request to turn the traffic light's color to green.
- To do above, we developed the algorithm of "IsInRegion".  
"IsInRegion" determine whether the vehicle position is inside the region or outside the region.
- In the current algorithm of "IsInRegion", we must assign the 4 corners of a region like (leftTop,leftBottom,rightBottom,rightTop).  
This means we must care the coordinate order of corner points.
  
### Problem

- The current algorithm of "IsInRegion" can't work well, if we defined the region incorrectly.
  - Furthermore the likelihood of defining the region incorrectly is high,  
  because we must care the coordinate order of the corner points.  
  (Which point is the left or right or top or bottom? watching map.)

## Considered Options

| Option about "IsInRegion" | The likelihood of<br> defining region incorrectly <br> (The Problem) |What do we care<br> to define region?|Description|Extensibility|
|:---:|:---:|:---:|:---:|:---:|
|The current Algorithm| High |draw one-stroke without any intersections. <br> <span style="color: red; "> AND care the coordinate order of corner points</span>.|(leftTop,leftBottom,rightBottom,rightTop)<br> <span style="color: red; ">  watching map.</span>|-|
| The Crossing Number Algorithm| Low |draw one-stroke without any intersections .|(leftFront,leftBack,rightBack,rightFront) <br>  <span style="color: orange; "> from the approaching vehicle view</span>.| we can extend the region shape <br> from Quadrilateral to the polygon |

- [The current Algorithm](https://confluence.tri-ad.tech/display/FSSTRAF/Green+Signal+Request+Algorithm#:~:text=R5%E7%AF%84%E5%9B%B2%E8%A8%88%E7%AE%97-,%E5%8F%B3%E5%9B%B3%E3%81%AE%E8%A6%81%E9%A0%98%E3%81%A7%E5%88%A4%E5%AE%9A%E3%81%99%E3%82%8B,-%EF%BC%88%E9%AB%98%E5%8E%9F%E3%81%95%E3%82%93%E6%A1%88) is tentatively adopted in the past.
- [The Crossing Number Algorithm](https://www.nttpc.co.jp/technology/number_algorithm.html) is a common and mainstream method to determine whether the point is inside the polygon or outside the polygon.
  
  - Basically, the flow of the algorithm is as below.  
    1. draw the horizontal line from the point to the right.
    2. count the number of intersections between the horizontal line and the polygon.
    3. If the count is odd, it' inside. If the count is even, It's outside.
  - In addition, we need to care some concerns. (Please read [Crossing Number Algorithm](https://www.nttpc.co.jp/technology/number_algorithm.html) in detail)
    - Our code is needed to be designed to handle all of those concerns mentioned above.

## Decision Outcome

- We change the algorithm to the [The Crossing Number Algorithm](https://www.nttpc.co.jp/technology/number_algorithm.html).
  - The algorithm is drawn in the "domain/region/regionOfInterest.go"

## Note

- 2023-10-27 : Drafted, Originator: Yuichi Takahashi