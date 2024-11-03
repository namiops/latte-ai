# Dashboards and related things

## TL;DR

1. Create a __Grafana Folder__ following the [example](examples/grafanafolder-example.yaml).
2. Create a dashboard in a JSON file. Make sure to use the correct data source. [example](examples/example-dashboard.json) The example is for a dashboard that uses "Prometheus" as data source.
3. Create a Kustomization with a configMapGenerator for your JSON file(s) following the [example](examples/kustomization.yaml)
4. Create a GrafanaDashboard definition in your namespace following the [example](examples/grafanadashboard-example.yaml).

When you update your dashboard JSON, increase the version of __agora_version__ in the grafanaDashboard.

## Dashboards

### Dashboard definition
Dashboards can be defined in _any namespace_. The Grafana in Observability will pick them up if they are set up using the grafna operator.

#### Grafana dashboard code
There are multiple ways to store a grafana dashboard.

##### json file loaded into configmap
##### pro
  - clean separation between kubernetes related code and the dashboard
  - easy to edit json files
##### contra
  - Need to manually increment the _agore\_version_ in the grafanaDashboard

##### json code directly in grafanaOperator
##### Pro
  - no need update _agora\_version_. The operator will automatically pick up updates.
##### Contra
  - Writing valid JSON in a yaml file is harder.

#### Data Sources referenced in dashboards
Note that you must not hard code UIDs data sources in the dashboard, because the provider will reject this.
Set all datasources used by the dashboard in spec.datasources.[] and use the variable in your dashboard code.

### commented example
```yaml
apiVersion: integreatly.org/v1alpha1
kind: GrafanaDashboard
metadata:
name: example                    # <-- Any name that does not already exist
namespace: exampleapp            # <-- your namespace
labels:
app: grafana
grafana_dashboard: "1"           # <-- This label must be set
annotations:
kustomize.toolkit.fluxcd.io/ssa: merge
spec:
  configMapRef:
    name: example-dashboard      # <-- Must match the name of the configmap
    key: example-dashboard.json  # <-- Must match the filename in the configmap
  customFolderName: "Example Application Dashboards"
  datasources:                   # <-- Set your datasources that your dashboard needs here.
    - inputName: "DS_PROMETHEUS"
      datasourceName: "Prometheus"
  json: >
      {
      "agora_version": "1"       # <-- Increase this version when you change your json
      }
```

### Exporting dashbord to JSON
Export your dashboard to json to import it into a configmap:
  - Click "Share dashboard or panel" symbol (-<:)
  - Select the "Export" tab
  - Turn _ON_ "Export for sharing externally"
  - "Save to file"

### Other methods
The grafana operator supports more methods to import dashboards. See the
[official documentation](https://github.com/grafana-operator/grafana-operator/blob/master/documentation/dashboards.md)
and [examples](https://github.com/grafana-operator/grafana-operator/tree/master/deploy/examples/dashboards)
for details.

### Further reading
  - For more details about dashboards refer to the [official documentation](https://github.com/grafana-operator/grafana-operator/blob/master/documentation/dashboards.md)
  - [Official Grafana Dashboard documentation](https://grafana.com/docs/grafana/latest/dashboards/)

## Dashboard Folders
These are the folders the dashboards are put into in Grafana.
They can simply be defined in the namespace.
If a dashboard is set to be placed into a folder that is not defined, the folder will be created with default values.

For more details about folders refer to the [official documentation](https://github.com/grafana-operator/grafana-operator/blob/master/documentation/folder_permissions.md).

### Pitfall

  - `GrafanaDashboard` can not have `title` set to a string with spaces. Reconciling will fail with an error like
    ```
    [..] "the object has been modified; please apply your changes to the latest version and try again"
    ```

## Plugins
At the moment, Plugins can only be installed via Grafana ENV variable.
Required plug-ins must be added to the GF\_INSTALL\_PLUGINS environment variable in configmap-grafana-plugins.yaml

## Data sources
Data sources are defined using _GrafanaDataSource_.
See the [official documentation](https://github.com/grafana-operator/grafana-operator/blob/master/documentation/datasources.md) for details.

## Cross Namespace creation of resources using the Grafana operator
You can create `GrafanaDashboards`, `GrafanaFolders` etc. from outside the
`observability` namespace. For the operator to pick up such objects, you need
to add the correct matchers and _very importantly_ the following in your spec:

```
spec:
  allowCrossNamespaceImport: true
```
