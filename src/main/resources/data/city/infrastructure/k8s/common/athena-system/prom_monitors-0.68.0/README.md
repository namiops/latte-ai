# prom\_monitors-0.68.0

## Source
[bundle.yaml](https://github.com/prometheus-operator/prometheus-operator/releases/download/v0.68.0/bundle.yaml)

## Manual operation
```sh
kuberctl slice -f bundle.yaml -o ./
mv customresourcedefinition-servicemonitors.monitoring.coreos.com.yaml customresourcedefinition-podmonitors.monitoring.coreos.com.yaml ./prom_monitors-0.68.0
```

## TODO:
Write a script at some point
