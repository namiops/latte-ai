# About dashboards

## doc
To add dashboards (and folders) follow the [Grafana Operator API Documentation].

## notes

### cross namespaces
One not obvious setting is the setting `allowCrossNamespaceImport`. Set this to
`true`, if your dashboard is in a namspace other than `athena-dashboard`.

### datasources
To use `datasources` as a variable in your dashboard, set it in the
`GrafanaDashboard` spec. See the example.

### configmaps
Use configmaps to store your dashboards. This makes it easy to import and mange them.
You can have one configmap holding several dashboards, but it is easier to have
one configmap per dashboard (json file).

## Example

```yaml
apiVersion: grafana.integreatly.org/v1beta1
kind: GrafanaDashboard
metadata:
  name: example
  labels:
    app: grafana
    grafana_dashboard: "1"
spec:
  allowCrossNamespaceImport: true
  instanceSelector:
    matchLabels:
      app.kubernetes.io/name: grafana
  configMapRef:
    name: example
    key: example.json
  folder: "examples"
  datasources:
    - inputName: "DS_LOKI"
      datasourceName: "Loki"
    - inputName: "DS_PROMETHEUS"
      datasourceName: "Prometheus"
```

<!-- Below are the links used in the document -->
[Grafana Operator API Documentation]:https://github.com/grafana/grafana-operator/blob/master/docs/docs/api.md
