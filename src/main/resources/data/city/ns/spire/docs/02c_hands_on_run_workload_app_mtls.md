# Hands-on: Running External Workload Part 1: mTLS in App

In this page, we'll deploy a simple application that gets the X.509 SVID with the Golang Workload API SDK and use it to talk HTTP with a service running inside Agora over mTLS.

## Procedure

1. In the city repo, go to [ns/spire/docs-k8s/workload-mtls-app directory](https://github.com/wp-wcm/city/tree/main/ns/spire/docs-k8s/workload-mtls-app), deploy it, and get into the container.
    ```sh
    kubectx minikube

    cd <city-repo-dir>/ns/spire/docs-k8s/workload-mtls-app
    kubectl apply -k .

    kubectl -n spire-tutorial exec -it $(kubectl -n spire-tutorial get po -l app=workload-mtls-sample -ojsonpath='{.items[0].metadata.name}') -- sh
    ```
2. Within the container, build & run the program to get the X.509 SVID and send HTTP GET request to _Internal Workload_.
    ```sh
    # Don't worry about `open /go/src/go.mod: permission denied` error
    cd /go/src ; go mod tidy ; go build .
    ./main --internal-workload-url https://nginx.spire-tutorial.cityos-dev.woven-planet.tech:7029
    ```

The expected output is the Nginx Welcome html.

## Clean up

1. Delete the deployed resources in minikube.
    ```sh
    kubectx minikube

    cd <city-repo-dir>/ns/spire/docs-k8s/workload-mtls-app
    kubectl delete -k .
    ```

## Deep Dive

### [main.go](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/workload-mtls-app/go-client/main.go)

Let's take a look at the application program first.

```go
client, err := workloadapi.New(ctx, workloadapi.WithAddr("unix://"+*udsPath))
```

This is where the spire-agent workload API client is set up. You specify the Unix Domain Socket path where spire-agent exposes its API.

```go
trustDomain, err := spiffeid.TrustDomainFromString(*trustDomainString)
// ...omitted...
bundles, err := client.FetchX509Bundles(ctx)
// ...omitted...
bundle, err := bundles.GetX509BundleForTrustDomain(trustDomain)
// ...omitted...
caCert, err := bundle.Marshal()
```

This is where the root CA certificate for _Internal Workload_ is fetched. The follwoing http client will regard this certificate as the trusted root.

```go
svid, err := client.FetchX509SVID(ctx)
```

The former main dish of this sample program -- fetching the X.509 SVID here.

```go
certs := [][]byte{}
for _, cert := range svid.Certificates {
	certs = append(certs, cert.Raw)
}

rootCAs := x509.NewCertPool()
rootCAs.AppendCertsFromPEM(caCert)
```

Some conversion logic to adopt Spire SDK data structure to what http client expects.

```go
httpClient := &http.Client{
	Transport: &http.Transport{
		TLSClientConfig: &tls.Config{
			Certificates: []tls.Certificate{
				{
					Certificate: certs,
					PrivateKey:  svid.PrivateKey,
				},
			},
			RootCAs:    rootCAs,
			MinVersion: tls.VersionTLS13,
		},
	},
}
```

And configuring the http client.

```go
resp, err := httpClient.Get(*internalWorkloadUrl)
```

The former main dish of this sample program -- sending GET request to _Internal Workload_.

!!! warn

    One caveat is, as introduced in "What's SPIFFE/SPIRE?" page of this tutorial as one of the benefits of Spire, the SVIDs are all short-lived. This means you have to implement a logic to redo `FetchX509SVID` as they expire.

    Envoy mTLS (which we'll be looking at in the next page) will largely ease this burden.

### [client.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/workload-mtls-app/go-client/client.yaml)

The client manifest file is almost just a usual deployment definition but the following is worth noting.

```yaml
spec:
  template:
    spec:
      # ...omitted...
      containers:
        - name: workload-mtls-app
          volumeMounts:
            - name: spire-agent-socket
              mountPath: /run/spire/sockets
              readOnly: true
      # ...omitted...
      volumes:
        - name: spire-agent-socket
          hostPath:
            path: /run/spire/client-sockets
            type: Directory
```

These are how the socket directory is mounted. Notice `hostPath` is the same as that of [agent.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/spire-agent/agent.yaml) mentioned in the previous page.
