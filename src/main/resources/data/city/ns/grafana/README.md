# Install
## Add repository to helm
```
# Grafana
# https://github.com/grafana/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
```

## Generate k8s manifests from helm chart
```
helm template -n grafana grafana grafana/grafana -f values/grafana.yaml > k8s/grafana.yaml
```
## Apply manifests to the kubernetes cluster
```
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/grafana.yaml
kubectl apply -f k8s/gateway.yaml
kubectl apply -f k8s/virtual-service.yaml
```

## Login
You can access the grafana at [http://grafana.woven-city.global](http://grafana.woven-city.global)
Initail administrator credential are admin/admin.

