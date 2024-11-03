# Speedway Proxy PoC

This is the original experimental version of the Speedway Proxy, that is necessary to enable kubectl to list pods,
services, deployments etc. in all namespaces. It is also required to list namespaces in the Speedway cluster. This is
due to security restrictions on SMC clusters that by default prevent these operations.

## Running the Proxy

For local execution, you need to run `kubectl` as a proxy to a the `agora-control-plane-dev-gc-0-apps-ap-northeast-1`
cluster context, ensure that dependencies are installed and then simply execute the `proxy.py` file:

```shell
kubectl proxy --port 5001
pip3 install -r requirements.txt
./proxy.py
```

The `kubectl` command will block, so run it and the proxy in separate shells. Then you can (in a third shell) use curl
to request a list of resources on global endpoints:

```shell
curl http://localhost:8080/api/v1/namespaces
```

To prove that the proxy is working, try the same curl command on port 5001, which will go directly to the Kubenetes
API, and it will return a 403 status.

Alternatively, you can run the proxy using the `startup.sh` script, but this is mostly meant to be used from within a
Docker image on the Speedway cluster.

## Production Proxy

Due to knowledge gained during the creation of the PoC version of the proxy, and the limitations imposed on us by the
use of the Flask framework, it has been decided to rewrite the proxy in Rust, using a completely different
architecture that can scale to hundreds of connections on a single server. The new prodction version of the proxy can
be found [here](../keystone).
