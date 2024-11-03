# Allow requests to a service from another service outside the cluster

**Question:** We want to authorize access to our service from a pre-defined set of clients from outside the cluster. How can Drako help us achieve out goal?

**Answer:** Create a `DrakoPolicy` using the [ClientGroup](../../../crd/DrakoPolicy/#clientgroup) policy type to list the services' [OAuth2 client IDs](https://www.oauth.com/oauth2-servers/client-registration/client-id-secret/) we want to authorize.

## How to

Let's say we want to let clients with client IDs `foo-client-id` and `bar-client-id` have access to our service `my-service-sa`.
We'd create the following `DrakoPolicy` with the name `client-group-oauth-policy`.

```yaml
--8<--
authorization_scenarios/service_outside_cluster/drakopolicy.yaml
--8<--
```

Note that the policies above do not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/service_outside_cluster/drakopolicybinding.yaml
--8<--
```

We give the binding the name `client-access-binding`, set `authenticationMode: Oauth2`, specify `Oauth2ClientId` as `clientAuthenticationModes`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.
As our `client-group-id-policy` policy is the only one specified in `decisionStrategy`, all requests but the ones from the members listed under `clientList` in `client-group-id-policy` get rejected.

## Resources

[ClientGroup documentation](../../../crd/DrakoPolicy/#clientgroup)  
[OAuth2 client IDs](https://www.oauth.com/oauth2-servers/client-registration/client-id-secret/)
