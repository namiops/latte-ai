# Notification Controller

>The Notification Controller is a Kubernetes operator, specialized in handling
inbound and outbound events.

The Notification controller dispatches events to external systems based on
event severity. Essentially the controller responsible for talking to Slack,
Teams, or Discord based on actions that flux has taken.

If you are on the WCM slack, #wcm-city-os-bots has events from our flux
clusters. These come from the Notification Controller.
