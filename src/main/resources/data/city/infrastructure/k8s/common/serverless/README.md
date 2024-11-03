# Namespace Serverless

This folder contains generated Knative manifests for Serverless Applications in Agora.

## Getting started

### Start with agoractl-serverless plugin
Agora provides you with the tool to quickly get started with serverless scaffold.

Please see [agoractl-serverless tool docs](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial/plugins/07_agoractl_serverless) for details.

Notes:
- If you don't have an initial image - [sockeye:v0.7.0](https://github.com/n3wscott/sockeye) will be used before image automation will kick in.
- While you can generate the code yourself via [agoractl-serverless tool](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial/plugins/07_agoractl_serverless), or you might as well simply fill a BUILD file and let Zebra automation take care of the rest.

## Sample generated application:
- [extended-service-sample](./demo/extended-service-sample)
- [minimal-service-sample](./demo/minimal-service-sample)

## Getting your serverless application endpoint

To get an endpoint of your application you'll need run a kubectl command (notice http -> https transition):

```bash
kubectl -n <NAMESPACE> get kservice <APPLICATION_NAME> -o jsonpath='{.status.url}' | sed "s/http:/https:/"
```

Example:
```bash
kubectl -n serverless get kservice new-service -o jsonpath='{.status.url}' | sed "s/http:/https:/"
```

To see full status use:
```bash
kubectl -n <NAMESPACE> get kservice <APPLICATION_NAME> -o yaml
```

Example:
```bash
kubectl -n serverless get kservice new-service -o yaml
```
