## Format of multi-valued query parameters (arrays)

For query parameters that can have multiple values (i.e. their schema is 
`type: array`), such as the `dataattrs` parameter on `GET /be/check_consent`, we
use `style: form` and `explode: true`. In short, you repeat the query parameter
for each value you want to add.  

For details on `style` and `explode`, see the
[OAPI docs here](https://swagger.io/specification/v3/#style-examples).

For example, if you want to check consent for user ID `tarou`, client 
`app-backend`, and data attributes `CITY_ADDRESS_ID` and `TENANT_ID`, the full
URL looks like this:

```
http://consent.consent.svc.cluster.local/v2alpha/check_consent?user=tarou&client=app-backend&dataattrs=CITY_ADDRESS_ID&dataattrs=TENANT_ID
```

## Agreements API

### Terminology
- **Agreement**: A legal contract that a service provider wants a user to sign (i.e. accept).
    For example, this could be a Terms of Use contract, or an agreement to processing of personal information.
  - An agreement is **pending** while the user has not signed it yet.
  - A user **signs** an agreement to show their acceptance of the terms in the agreement.
  - Consequently, a **signed** agreement has been accepted by the user.
- **Agreement group**: A group of agreements that are somehow related, for example because they all pertain to
    the same project, or the same experiment. This API does not care about the concrete relationship between
    the agreements in a group. Each Agreement can only belong to one single group. Groups cannot contain other groups.

### Further Notes
- This API does not offer a way to manage the agreements and their contents or the agreement groups. This
  management is handled out of band.
- This API does not offer a way for a user to withdraw their acceptance to an agreement.
