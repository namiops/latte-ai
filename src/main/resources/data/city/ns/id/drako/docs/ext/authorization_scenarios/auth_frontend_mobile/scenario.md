# Allow requests from a frontend or mobile application

**Question:** We want to authorize requests from a frontend or mobile application. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` according to our needs and create a `DrakoPolicyBinding` using the `SingleUseToken` or `Oauth2` [authentication mode](../../../crd/DrakoPolicyBinding/#authenticationmode)
if our frontend or mobile application supports [OAuth2](https://oauth.net/2/). If it does not, `Legacy` authentication mode is the fallback.

## How to

We need to create the following policy (let us call it `allow-all-policy`) to allow requests from everyone.

```yaml
--8<--
authorization_scenarios/auth_frontend_mobile/drakopolicy.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/auth_frontend_mobile/drakopolicybinding.yaml
--8<--
```

We give the binding the name `single-use-token-binding`, specify `SingleUseToken` as the `authenticationMode`, and list the `policies` this binding's `decisionStrategy` uses to
evaluate a request to allow or reject it.
As our `allow-all-policy` policy is the only one specified in `decisionStrategy`, all requests are allowed.

If our application does not suppport OAuth2, we replace `SingleUseToken` in the binding above with `Legacy`.
!!! warning
    In `Legacy` authentication mode, Drako will integrate our application with Woven ID, and once the user is logged in, Drako will set a session cookie (named `CITYCOOKIE`). However, we need to keep in mind that `Legacy` (as the name implies) is less secure and interoperable than OAuth2. If we are in the position to adapt the frontend or mobile application to use OAuth2 or if the third party application we are using already supports OAuth2, we should use
    OAuth2 as `authenticationMode`.

## Resources

- [chat application](https://github.com/wp-wcm/city/tree/main/ns/id/examples/chat) as an example of an application with a frontend and backend using Drako:
  - [frontend manifests](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-id-test/speedway/common/chat-frontend-0.1.0-manifest-0.1.0)
  - [backend manifests](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-id-test/speedway/common/chat-backend-0.1.0-manifest-0.1.0)
