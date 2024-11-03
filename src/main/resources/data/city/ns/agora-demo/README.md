## Deploy demo apps

- Deploy node-red and httpbin(custom) for demo

```
NS=agora-demo-1
kubectl apply -f ${NS}.yaml
helm template svc1 node-red-chart/ -n ${NS} | kubectl apply -n ${NS} -f -
helm template svc2 node-red-chart/ -n ${NS} | kubectl apply -n ${NS} -f -
helm template httpbin httpbin-chart/ -n ${NS} | kubectl apply -n ${NS} -f -
```

```
NS=agora-demo-2
kubectl apply -f ${NS}.yaml
helm template svc3 node-red-chart/ -n ${NS} | kubectl apply -n ${NS} -f -
helm template httpbin httpbin-chart/ -n ${NS} | kubectl apply -n ${NS} -f -
```


- patch-370564492268/*

  - Patch manifest to integrate with id/{keycloak,oauth2}
  - Because AWS 370564492268 is different from productive settings


- patch-minikube/*
  - create the gp2 storageclass as alias to keep campatibility with ec2 
