# Allow requests if the user ID in the request path is the same as of the authenticated user

**Question:** We want to allow requests to our service if the authenticated user and the user in a URL path matches. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the [MappingComparison](../../../crd/DrakoPolicy/#mappingcomparison) type
to specify `EqualsLoggedInUserId` and the name of the key that we assign a specific URI part to via [mapping](../../../crd/DrakoPolicyBinding/#mapping)
in the `DrakoPolicyBinding`.

## How to

We create the following policy (let us call it `logged-in-user-policy`) with overall type `MappingComparison` and sub-type `EqualsLoggedInUserId` with key `userId`.

```yaml
--8<--
authorization_scenarios/mapped_user/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/mapped_user/drakopolicybinding.yaml
--8<--
```

We give the binding the name `logged-in-user-binding` in our namespace `my-service-ns`, specify `Oauth2` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.  
The path `/api/v1/user/:userId` under `mapping.paths` extracts the URI part after `user/` and captures it under `userId`. The same name as the `key` in `mappingComparison` in our `logged-in-user-policy`.

Alternatively, `SingleUseToken` and `Legacy` `authenticationMode` may be used.  
See [DrakoPolicyBinding documentation](../../../crd/DrakoPolicyBinding/#authenticationmode) for details.

## Resources

[MappingComparison documentation](../../../crd/DrakoPolicy/#mappingcomparison)  
[mapping documentation](../../../crd/DrakoPolicyBinding/#mapping)
