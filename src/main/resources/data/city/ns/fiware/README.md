# Install
## Add repository to helm
```
# MongoDB
# https://github.com/bitnami/charts/tree/master/bitnami/mongodb
helm repo add bitnami https://charts.bitnami.com/bitnami

# Orion
# https://github.com/FIWARE/helm-charts
helm repo add fiware https://fiware.github.io/helm-charts

# CrateDB & QuantumLeap
# https://github.com/orchestracities/charts/tree/master/charts/crate
# https://github.com/orchestracities/charts/tree/master/charts/quantumleap
helm repo add oc https://orchestracities.github.io/charts/

```
## Generate k8s manifests from helm chart
```
# MongoDB
helm template -n fiware mongo bitnami/mongodb -f values/mongo.yaml > k8s/mongo.yaml
# Orion
helm template -n fiware orion fiware/orion -f values/orion.yaml > k8s/orion.yaml
# CrateDB
helm template -n fiware crate oc/crate -f values/crate.yaml > k8s/crate.yaml
# QuantumLeap
helm template -n fiware quantumleap oc/quantumleap -f values/quantumleap.yaml > k8s/quantumleap.yaml
```
## Apply k8s manifests
```
# Make new namespace
kubectl create ns fiware
kubectl -n fiware apply -f k8s/
```

## Delete the fiware depoyments
```
# Make new namespace
kubectl delete ns fiware
```

## !DO NOT! Install helm chart directory
You can do the abobe steps directly using following commands. 
However, it is recommended that you first generate the manifest and then apply it, as you will need to know what to deploy.
```
# Make new namespace
kubectl create ns fiware

# MongoDB
helm install -n fiware mongo bitnami/mongodb -f mongodb/values.yaml

# Orion
helm install -n fiware orion fiware/orion -f orion/values.yaml

# CrateDB
helm install -n fiware crate oc/crate -f cratedb/values.yaml

# QuantumLeap
helm install -n fiware quantumleap oc/quantumleap -f quantumleap/values.yaml
```

## !DO NOT! Delete what you installed
```
helm delete -n fiware mongo
helm delete -n fiware orion
helm delete -n fiware crate
helm delete -n fiware quantumleap
```
