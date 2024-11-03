# Allow requests from a specific frontend app in WovenApp to access a particular backend app

**Question:** We want to limit access to out backend app to a specific frontend app. How can Drako help us achieve our goal?

**Answer:** Create a `DrakoPolicy` using the [ClientGroup](../../../crd/DrakoPolicy/#clientgroup) type to specify the client ID of the frontend app in question.
Create another `DrakoPolicy` using the [Audience](../../../crd/DrakoPolicy/#audience) type to specify the client ID of your backend app.

## How to

We create the following policy (let us call it `woven-app-frontend-client-id-policy`) of type `ClientGroup` specifying the OAuth2 client ID of the frontend app under `clientList.value`.

```yaml
--8<--
authorization_scenarios/wovenapp_frontend_backend_access/drakopolicy_clientgroup.yaml
--8<--
```

We also create another policy named `wovenapp-backend-audience-policy` of type `Audience` listing the backend's client ID under `audience`.
```yaml
--8<--
authorization_scenarios/wovenapp_frontend_backend_access/drakopolicy_audience.yaml
--8<--
```

Note that the policy above does not mention our service account `my-service-sa`.
This is because the policy is connected to a specific service through a `DrakoPolicyBinding` as follows:

```yaml
--8<--
authorization_scenarios/wovenapp_frontend_backend_access/drakopolicybinding.yaml
--8<--
```

We give the binding the name `allow-api-options-method-from-outside-binding` in our namespace `my-service-ns`, specify `Oauth2` as the `authenticationMode` and `Oauth2ClientId` as `clientAuthenticationModes`,
point `destinationServiceAccounts` to our service account `my-service-sa`, and list the `policies` this binding's `decisiontrategy` uses to
evaluate a request to allow or reject it.  
As only requests satisfying both policies should be allowed, we combine both using the logical AND operation `&&` in the binding's `decisionStrategy`.

## Resources

[ClientGroup documentation](../../../crd/DrakoPolicy/#clientgroup)  
[Audience documentation](../../../crd/DrakoPolicy/#audience)  
[decisionStrategy documentation](../../../crd/DrakoPolicyBinding/#policies-and-decisionstrategy)
