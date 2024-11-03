# loadtest
Experiments to making load testesting tools

In this experiment, I chose `playwright` to manipulate browsers.

## install
```
git clone https://github.tri-ad.tech/cityos-platform/loadtest.git
npm install
```

## create local cluster
```
/infrastructure/k8s/local/bin/bootstrap
```
## deploy service(sample-app)
```
kubectl apply -k ../sample_app/app/k8s
```
or you can deploy it by FluxCD

## set up networking
make tunnel to access city-ingress
```
minikube tunnel
```
add entry to /etc/hosts
```
<city-ingress-ip-address> id.woven-city.local id-test-web-app.woven-city.local
```

## Run test
```
npx playwright test --config=./config/config_for_sample_app.ts
```

## Run testcase generator
```
npx ts-node ./codegen.ts
```


