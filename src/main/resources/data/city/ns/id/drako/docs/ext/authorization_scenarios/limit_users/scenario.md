# Allow requests to a service from certain users only

**Question:** We want to limit access to our service to a pre-defined set of users. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the [Group](../../../crd/DrakoPolicy/#group) policy type to list the [DrakoGroup](../../../crd/DrakoGroup) of the users you'd like to give access to your service.

## How to

Let's say we want to let users with Woven IDs `alice-aba-112` and `bob-xas-302` have access to our service `my-service-sa`.
We'd create the following `DrakoGroup` with the name `my-service-access-group` in our namespace `my-service-ns`.

```yaml
--8<--
authorization_scenarios/limit_users/drakogroup.yaml
--8<--
```

We'd also create the following `DrakoPolicy` with the `Group` type to link to your `my-service-access-group` `DrakoGroup` and name it `give-alice-and-bob-access-policy`.

```yaml
--8<--
authorization_scenarios/limit_users/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention your service account `my-service-sa`.
The policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/limit_users/drakopolicybinding.yaml
--8<--
```

We give the binding the name `alice-and-bob-access-binding`, specify `SingleUseToken` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.  
As our `alice-and-bob-access-policy` policy is the only one specified in `decisionStrategy`, all requests but the ones from `alice-aba-112` and `bob-xas-302` are rejected.  

Alternatively, `Oauth2` and `Legacy` `authenticationMode` may be used.  
See [DrakoPolicyBinding documentation](../../../crd/DrakoPolicyBinding/#authenticationmode) for details.

## Resources

[DrakoGroup documentation](../../../crd/DrakoGroup)  
[Group documentation](../../../crd/DrakoPolicy/#group)
