# observability

# Sources

## deployments

Below the sources for each deployment

| Application         | Source                                                                | Type        |
| ------------------- | --------------------------------------------------------------------- | ----------  |
| agora-injected-prom | bin/import                                                            | HelmRelease |
| dcgm-exporter       | bin/import                                                            | HelmRelease |
| event-exporter      | bin/import                                                            | HelmRelease |
| grafana             | N/A we use the grafana operator now                                   | Grafana[^1] |
| jaeger              | https://github.com/istio/istio/blob/master/samples/addons/jaeger.yaml | Deployment  |
| kiali               | bin/import                                                            | HelmRelease |
| kubeshark           | bin/import                                                            | Deployment  |
| nginx               |                                                                       | Deployment  |
| prometheus          | bin/import                                                            | HelmRelease |
| tempo               | bin/import                                                            | HelmRelease |

[^1]: provided by the operator in observability-system.

## Dashboards

For the details about the dashboards see the [README.md](dashboards/README.md) file under the dashboards directory.

