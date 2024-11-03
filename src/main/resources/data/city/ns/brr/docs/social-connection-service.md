# Social Connection Service

The Social Connection service is a service that manages social connections among users.
Unlike typical social networking services like Twitter and Instagram, it won't
provide features like posts and timelines. The purpose of the service is to
provide fundamentals to support communities of users that exist inside
(and possibly also outside of) Woven City

In the meantime, we are going to focus on providing only mutual connection features.

## Further Reading and Resources
For further information about the Social Connection service, please see the links below:

- For information on all the API endpoints that BURR offers, see the
[Social Connection service API documentation](https://developer.woven-city.toyota/catalog/default/api/burr-social-connection/definition#).
- For further information and discussions, see the [TN-0289](https://docs.google.com/document/d/1sPTESW-R2XHkpDQE2XDKgzuiXC_ox-CKihyflXHgfS8/edit)

## Status

The Social Connection service is currently in the alpha stage of development. The
provided feature set is very limited and somewhat incomplete. It will be
continuously evolving.

There is also a possibility that information stored may be dropped at
at some point in time due to API version upgrades or redesigns of the data
schema. Of course, we will make an effort to migrate the data to the new one,
but there is no guarantee we can always do that.

## Consent Integration

Support for consent is planned. However, currently user consent is not yet considered/handled.

## Getting Started

### Prerequisite

Please make sure that users you are going to interact with via the Social Connection service have been registered in BURR. See [BURR documentation](https://developer.woven-city.toyota/docs/default/Component/brr-service/).

### Base URLs

```
# Cluster internal
http://social-connection.brr.svc.cluster.local/social-connection/v1alpha
# Accessible via ingress
https://brr.cityos-dev.woven-planet.tech/social-connection/v1alpha

# We offer two separate but identical service instances as same as BURR service.
# Cluster internal
http://social-connection.brr-b.svc.cluster.local/social-connection/v1alpha
# Accessible via ingress
https://brr-b.cityos-dev.woven-planet.tech/social-connection/v1alpha
```

### Establish Mutual Connection between Users

You can manage mutual connections with the Connection API. To create a connection, use the endpoint below:

- [`POST /connection/{wovenId}/{otherWovenId}`](https://developer.woven-city.toyota/catalog/default/api/burr-social-connection/definition#/default/post_connection__wovenId___targetWovenId_)

The order of woven IDs in the path doesn't matter.

After that, you can fetch a list of profiles of users who are connected mutually with the endpoint below:

- [`GET /connection/{wovenId}`](https://developer.woven-city.toyota/catalog/default/api/burr-social-connection/definition#/default/get_connection__wovenId_)

There is also delete connection endpoint:

- [`DELETE /connection/{wovenId}/{otherWovenId}`](https://developer.woven-city.toyota/catalog/default/api/burr-social-connection/definition#/default/delete_connection__wovenId___targetWovenId_)

### Notes for Establishing Mutual Connection

We're planning to have a mutual connection feature modeled with Facebook but not
Twitter, which means a flow of requesting and accepting is required to establish
a connection. The current version of connection API provides a pretty naive one
with the create connection endpoint that doesn't have this concept. When we
support it, the create connection endpoint will be deprecated.
