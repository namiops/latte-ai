# Allow requests to a service from another service inside the cluster

**Question:** We want to authorize access our my service from a pre-defined set of clients from inside the same Agora kubernetes cluster. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the [ClientGroup](../../../crd/DrakoPolicy/#clientgroup) policy type to list the services' [SPIFFE IDs](https://developer.woven-city.toyota/docs/default/component/spire/00_whats_spiffe_spire/) you want to authorize.

## How to

Services running inside an Agora kubernetes cluster use [SPIFFE IDs](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/) to identify themselves securely when communicating with other services. For an Agora cluster, the SPIFFE ID of the source service initiating the request follows the format: _spiffe://cluster.local/ns/**SOURCE_NAMESPACE**/sa/**SOURCE_SERVICE_ACCOUNT**_.

For instance, let's say we want to let clients with SPIFFE IDs `spiffe://cluster.local/ns/foo-namespace/sa/service-foo` and `spiffe://cluster.local/ns/bar-namespace/sa/service-bar` have access to our service `my-service-sa`.
We'd create the following `DrakoPolicy` with the name `client-group-spiffe-policy`.

```yaml
--8<--
authorization_scenarios/service_inside_cluster/drakopolicy.yaml
--8<--
```

!!! Note
    The `ClientGroup` mentions the SPIFFE IDs for the source client, not our destination service.

Then, finally, we can use the `ClientGroup` policy by creating a `DrakoPolicyBinding` as follows:


```yaml
--8<--
authorization_scenarios/service_inside_cluster/drakopolicybinding.yaml
--8<--
```

We can choose other binding and policy names, but in the example above we name the binding `spiffe-access-binding`, specify `SpiffeId` as `clientAuthenticationModes`, set `authenticationMode: None` so that the service can be called without a user being logged in, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.
As our `client-group-spiffe-policy` policy is the only one specified in `decisionStrategy`, all requests from clients outside the `clientList` in `client-group-spiffe-policy` will get rejected.

## Resources

[ClientGroup documentation](../../../crd/DrakoPolicy/#clientgroup)  
[SPIFFE IDs](https://developer.woven-city.toyota/docs/default/component/spire/00_whats_spiffe_spire/)
