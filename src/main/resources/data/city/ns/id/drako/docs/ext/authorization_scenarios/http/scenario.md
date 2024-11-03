# Allow requests to certain HTTP paths and/or HTTP methods

**Question:** We want to limit access to our service through a pre-defined set of HTTP paths an/or HTTP methods. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicyBinding` to specify the [paths](../../../crd/DrakoPolicyBinding/#paths) value
and/or the [methods](../../../crd/DrakoPolicyBinding/#http-methods) value according to your needs.

## How to

We create the following policy (let us call it `allow-all-policy`) in order to allow every client to connect to our service `my-service-sa`.

```yaml
--8<--
authorization_scenarios/http/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/http/drakopolicybinding.yaml
--8<--
```

We give the binding the name `http-binding`, specify `SingleUseToken` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.  
This binding is matched (i.e. is used to evaluate a request) when a requests goes to one of the paths specified in `paths` on `my-service-sa` and is using one of the HTTP methods in `methods`.
Though, the binding does not match requests going to `/api/v1/profile/admin` as specified in [excludePaths](../../../crd/DrakoPolicyBinding/#excludepaths).

The matched requests will then get evaluated according to `decisionStrategy`.

!!! Note
    If `methods` is not used, the binding matches on all HTTP requests. Likewise, if `paths` is not defined, the binding matches on all paths.

## Resources

[paths documentation](../../../crd/DrakoPolicyBinding/#paths)  
[methods documentation](../../../crd/DrakoPolicyBinding/#http-methods)  
[excludePaths documentation](../../../crd/DrakoPolicyBinding/#excludepaths)
