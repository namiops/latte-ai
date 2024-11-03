# Alloy trouble shooting

## Web-UI
You can get access to the WEB-UI of alloy running on port 12345 by using port-forwarding.

This can be useful to debug problems with metric scraping or log collection etc.

Note: In speedway you need to do that on the _host cluster_:

Example for Speedway prod:

```sh
k -n agora-observability-prod --as agora-observability-prod-admin port-forward service/grafana-k8s-monitoring-alloy 12345:12345
```
