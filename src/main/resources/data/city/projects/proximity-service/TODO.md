[x] Mocked endpoints (Monday/Tue)

[x] Skeleton (Tue/Wed)

[] Focus on docker-compose with Tile38 (Wed)

[x] Integrate with Tile38 (Thu)

[] Make the experiment route work first (Thu / Fri)

[] Make the resident route work next (Fri / Mon)

[] Then the CLP/demo components (Mon/Tue)
 - see replay/mod.rs ... instructions in there.
 - 
[] Health is optional (nice to have)


Notes:
 - abstraction layer "flattens" the different data and insights into something "uniform". will think about this a bit more
 - statistics / ML : is the subcomponent which needs to interact with ML models. (classification, segmentation, clustering, anomaly detection, deep learning eventually ... most all via REST API)
   - the gym lover ... propensity + multi-seasonality (goes to gym on Monday at 6pm)
   - the coffee lover ... has to have their coffee on workdays.
 - heuristics : non ML historical data. 
   - matching with a route from their history  ... some people always walk on some routes/streets.
   - matching a behavioral trait (not identified or understood by ML) ... some people always go into a specific shop
   - access camera feeds, badge scans, lat/long via "location-services" (in WV)
 - decisioning
   - will take everything into account
   - including map data, calendar data, historical heuristics data and statistics via ML models decisioning.
   - and will attempt to decide what is the destination (and which one was the Source of Truth)
   - decision tree ensemble style
 - lat/long tracking in reality has a lot of issues
   - spotty data
   - imprecise data
   - no signal / turned off / airplane / ...
   - so usually MDiscriminative (RNN, CRF, CTC) vs. Generative (HMM ... markov) needs to be used

Lets keep it super simple for now!!!!

 - define 4 geo fences in tile-38
 - pay attention to the VELOCITY with which people cross the boundaries into geo-fences. 
 - so this becomes a decay function in a time-series formula. ... one element with weight per ring.
 - 

e.g.
at T: enters the 4th (outer) fence. prob 25%
at T+1 ... till T+10: stays in 4th ... prob drops 25-23-21-19 for ring-4, and slowly drops for ring-3 as well
at T+11 ... enters in 3rd ... + decayed 4
at T+15 ... enters in 2nd fence ... + d3 + d4
at T+20 ... enters into 1st fence + d2 + d3 + d4 

i will come up with the exact decay formula on Tuesday.

Thanks!