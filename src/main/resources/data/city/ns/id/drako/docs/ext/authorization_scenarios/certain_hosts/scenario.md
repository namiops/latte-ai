# Allow requests to certain hosts

**Question:** We want to allow requests to pre-defined hosts. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicyBinding` using the [destinationHosts](../../../crd/DrakoPolicyBinding/#destinationhosts) field to list the hosts we'd like to allow requests to.

## How to

We need to create the following policy (let us call it `allow-all-policy`) in order to allow every client to connect to our service `my-service`.

```yaml
--8<--
authorization_scenarios/certain_hosts/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/certain_hosts/drakopolicybinding.yaml
--8<--
```

We give the binding the name `hostname-filter` in our namespace `my-service-ns`, specify `Oauth2` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.  
As we list `my-service-host.woven-city-api.toyota` under `destinationHosts`, this binding will only be applied to requests containing that destination host name. If that is the case, and because `allow-all-policy` policy is the only one specified in `decisionStrategy`, any request to that host name will be allowed.

Alternatively, `SingleUseToken` and `Legacy` `authenticationMode` may be used.  
See [DrakoPolicyBinding documentation](../../../crd/DrakoPolicyBinding/#authenticationmode) for details.

## Resources

[destinationHosts documentation](../../../crd/DrakoPolicyBinding/#destinationhosts)
