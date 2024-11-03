# Allow CORS requests

**Question:** We want to allow CORS requests to our service. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the [Source](../../../crd/DrakoPolicy/#source) type to specify the agora private ingress.
Use that policy to allow the pre-flight `OPTIONS` request via [methods](../../../crd/DrakoPolicyBinding/#http-methods)
in the `DrakoPolicyBinding`.

## How to

We create the following policy (let us call it `allow-request-via-agora-private-ingress`) of type `Source` specifying the agora private ingress `namespace` and `serviceAccount`.

```yaml
--8<--
authorization_scenarios/cors/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/cors/drakopolicybinding.yaml
--8<--
```

We give the binding the name `allow-api-options-method-from-outside-binding` in our namespace `my-service-ns`, specify `None` as the `authenticationMode`,
`destinationServiceAccounts` points to our service account `my-service-sa`, and list the `policies` this binding's `decisionStrategy` uses to evaluate a request to allow or reject it.  
The binding matches on `OPTIONS` requests to `/api/*` as specified in `methods` and `paths`, respectively, and thus allows the CORS `OPTIONS` pre-flight request.

## Resources

[Source documentation](../../../crd/DrakoPolicy/#source)  
[methods documentation](../../../crd/DrakoPolicyBinding/#http-methods)
