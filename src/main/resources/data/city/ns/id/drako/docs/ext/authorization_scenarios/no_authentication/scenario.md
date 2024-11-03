# Allow requests without authentication

**Question:** We want to allow access to our service without authentication. How can Drako help us achieve our goal?

**Answer:** Not using authentication is not recommended by default.
We'll need to get an approval for our service from the [Woven City Product Security Team](https://security.woven-planet.tech/processes/policy-exception-and-risk-acceptance-process/).

Given that we have received permission to disable authentication for our service, we create a `DrakoPolicyBinding` using [authenticationMode: None](../../../crd/DrakoPolicyBinding/#authenticationmode)
and the [AllowAll](../../../crd/DrakoPolicy/#allowall-or-denyall-static-policies) `DrakoPolicy` type.
Additionally, we need to add our host to the whitelist for [prod](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-id/speedway/prod/3-drako/patch-deployment-drako-env.yaml#L62)
and [dev](https://github.com/wp-wcm/city/blob/76fda18257f78ac8ac503bf09b2d87147ea12693/infra/k8s/agora-id/speedway/dev/3-drako/patch-deployment-drako-env.yaml#L60).

## How to

We create the following policy (let us call it `allow-all-policy`) to let users have access to your service `my-service-sa` without authentication.

```yaml
--8<--
authorization_scenarios/no_authentication/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/no_authentication/drakopolicybinding.yaml
--8<--
```

We give the binding the name `no-auth-binding`, specify `None` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.
As our `allow-all-policy` policy is the only one specified in `decisionStrategy`, all requests are allowed.

## Resources

[authenticationMode documentation](../../../crd/DrakoPolicyBinding/#authenticationmode)  
[AllowAll documentation](../../../crd/DrakoPolicy/#allowall-or-denyall-static-policies)  
[prod whitelist](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-id/speedway/prod/3-drako/patch-deployment-drako-env.yaml#L62)  
[dev whitelist](https://github.com/wp-wcm/city/blob/76fda18257f78ac8ac503bf09b2d87147ea12693/infra/k8s/agora-id/speedway/dev/3-drako/patch-deployment-drako-env.yaml#L60)
