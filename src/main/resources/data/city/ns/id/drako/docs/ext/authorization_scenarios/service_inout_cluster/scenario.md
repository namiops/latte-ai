# Allow requests to a service from other service inside and outside the cluster

**Question:** We want to authorize access to our service from a pre-defined set of clients from inside and from a pre-defined set of clients from outside the same Agora kubernetes cluster. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the `ClientGroup` policy type to list the [SPIFFE IDs](https://developer.woven-city.toyota/docs/default/component/spire/00_whats_spiffe_spire/) of the clients inside the cluster
and the [OAuth2 client IDs](https://www.oauth.com/oauth2-servers/client-registration/client-id-secret/) of the clients outside the cluster that we want to authorize.

## Approach 1

We describe how to [authorize requests to a service from another service outside the cluster](../../service_outside_cluster/scenario) and how to [authorize requests to a service from another service inside the cluster](../../service_inside_cluster/scenario). These two policies are:

```yaml
--8<--
authorization_scenarios/service_outside_cluster/drakopolicy.yaml
--8<--
```

and

```yaml
--8<--
authorization_scenarios/service_inside_cluster/drakopolicy.yaml
--8<--
```

Note that the policies above do not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/service_inout_cluster/drakopolicybinding.yaml
--8<--
```

where we list both policies, `client-group-spiffe-policy` and `client-group-oauth-policy` under `policies` and use them in this binding's `decisionStrategy`
to allow clients access who are in one of the two `clientList`s. As we have to authorize clients by `SpiffeId` and `Oauth2ClientId`, we need to list both under `clientAuthenticationModes`.
`destinationServiceAccounts` points to our service account `my-service-sa`.

## Approach 2

 Instead of using two separate policies as in [Approach 1](#approach-1), we can create a single `DrakoPolicy` that combines the two previous ones' `clientList`s

```yaml
--8<--
authorization_scenarios/service_inout_cluster/drakopolicy.yaml
--8<--
```

which simplifies the `policies` listing and `decisionStrategy` of the `DrakoPolicyBinding`

```yaml
--8<--
authorization_scenarios/service_inout_cluster/drakopolicybinding2.yaml
--8<--
```

## Resources

[decisionStrategy documentation](../../../crd/DrakoPolicyBinding/#policies-and-decisionstrategy)
