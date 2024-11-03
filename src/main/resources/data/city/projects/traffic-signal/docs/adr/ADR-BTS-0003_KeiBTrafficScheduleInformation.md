# ADR-BTS-0002 Kei-B Traffic Schedule Information

| Status | Last Updated |
|---|---|
|Drafted| 2023-10-30 |

## Context and Problem Statement

### Context

- Backend of Traffic Signal must subscribe the "Traffic Schedule Information" from Traffic Signal Controller  
in order to ...

  1. publish that to vehicles.  
  2. determine whether to publish Green Signal Extension Request to Traffic Signal Controller.

- "Traffic Schedule Information" from Traffic Signal Controller follows the format ["Kei-B(警b)"](https://drive.google.com/file/d/13o8RvgGZpGArjHAclBdeWPiowkh2UyMu/view) that is the common message standard.
  - Kei-B(警b) is defined by the Japan's national project [SIP](https://www.sip-adus.go.jp/rd/#r3).
  - Kei-B(警b)(maybe) can cover all possible signal patterns in Japanese traffic environment.
  
### Problem

- "警b" has many kinds of date items, but we want to treat only the necessary items, not all, in order to keep our backend as simple as possible.
In this ADR, we define(pick) necessary items for our system from all 警B defined items.

## Decision Outcome

- The necessary items we pick up are listed below. ( <span style="color: red; ">Note:This is still under construction, not a finalized version.</span> )

|#| item group | item | item<br>(English)| Starting bit position<br>(0~) | length of a bit |code| Usage.1 <br> Publish to vehicles| Usage.2 <br> Green Signal Extension Request | 
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|1|DF_提供点管理番号|DE_都道府県コード|provisionCode|0|bin(8)|D-1|?|?|
|2|DF_提供点管理番号|DE_提供点種別コード|offerPointTypeCode|8|bin(1)|D-2|?|?|
|3|DF_提供点管理番号|DE_交差点ID/単路ID|intersectionID|9|bin(47)|B-1|required|required|
|4|-|DE_システム状態|systemStatus|104|bin(8)|E-1|required ?|required ?|
|5|DF_作成日時|DE_年|year|120|bin(8)|A-1|required|required|-|
|6|DF_作成日時|DE_月|month|136|bin(8)|A-2|required|required|-|
|7|DF_作成日時|DE_日|day|144|bin(8)|A-3|required|required|-|
|8|DF_作成日時|DE_時刻(時)|hour|152|bin(16)|A-4|required|required|-|
|9|DF_作成日時|DE_時刻(分)|minute|160|bin(8)|A-5|required|required|-|
|10|DF_作成日時|DE_時刻(秒)|second|168|bin(8)|A-6|required|required|-|
|11|DF_作成日時|DE_時刻(10ミリ秒)|10mSec|176|bin(8)|A-7|required|required|-|
|12|-|DE_車灯器数|numOfLightsForVehicle|200|bin(8)|B-5|required|required|-|
|13|-|DE_歩灯器数|numOfLightsForPedestrian|208|bin(8)|B-6|required|required|-|
|14|-|DE_サービス方路数|numOfServiceApproaches|224|bin(8)|C-12|required|required|-|
|15|DF_サービス方路信号情報|DE_方路ID<br>(Multiple instances)|serviceApproachId|232 +<br>*index of 方路ID* ×<br>( 24 + 16×(*DE_車灯器数*+*DE_歩灯器数*) )|bin(8)|B-2|required|required|-|
|16|DF_サービス方路信号情報|DE_車灯器情報ポインタ<br>(Multiple instances)|pointerOfLightForVehicle|*Starting bit position of each DE_方路ID* +<br>24 + <br> 16 × *index of related 車灯器ID*|bin(16)|E-9|required|required|-|
|17|DF_車両灯器情報|DE_車灯器ID<br>(Multiple instances)|lightForVehicleId|DE_車灯器情報ポインタ|bin(4)|B-5|required|required|
|18|DF_車両灯器情報|DE_灯色出力変化数<br>(Multiple instances)|numOfColorChanges|DE_車灯器情報ポインタ + 4|bin(4)|E-11|not required|required ?|
|19|DF_車両灯器情報|DE_丸信号灯色表示<br>(Multiple instances)|colorOfRoundSignal|DE_車灯器情報ポインタ + 8|bin(8)|E-12|required|required|
|20|DF_車両灯器情報|DE_青矢信号表示方向<br>(Multiple instances)|directionOfGreenArrowSignal|DE_車灯器情報ポインタ + 16|bin(8)|E-13|required|required|
|21|DF_車両灯器情報|DE_カウントダウン停止フラグ<br>(Multiple instances)|countdownStopFlg|DE_車灯器情報ポインタ + 24|bin(1)|E-14|not required|required<br>(Whether Step1 continues 30sec or not ?)|
|22|DF_車両灯器情報|DE_最小残秒数（0.1秒）<br>(Multiple instances)|minRemainingTime100msec|DE_車灯器情報ポインタ + 25|bin(15)|E-15|not required|not required ?|
|23|DF_車両灯器情報|DE_最大残秒数（0.1秒）<br>(Multiple instances)|maxRemainingTime100msec|DE_車灯器情報ポインタ + 40|bin(16)|E-16|not required|required ?|


## Note
- Starting bit position is calculated in [here](https://docs.google.com/spreadsheets/d/11is6rVOnO3d8E3V9AjmJzw39gHAcRzVNOU--FTUa_aU/edit?usp=sharing).
- reference
  - Answer from Nippon Signal inc.([here](https://triadjp01.sharepoint.com/:x:/r/sites/traffic-signal--wa/_layouts/15/Doc2.aspx?action=edit&sourcedoc=%7B03f37c3e-07a2-475a-af9f-d9721ae8a346%7D&wdOrigin=TEAMS-ELECTRON.teamsSdk_ns.bim&wdExp=TEAMS-CONTROL&wdhostclicktime=1698632556209&web=1))
- 2023-10-30 : Drafted, Originator: Yuichi Takahashi