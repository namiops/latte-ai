# Superset embedded dashboard - backend

## What does this do?

Embedded dashboards need a 'guest token' to be used. This token has a short lifetime,
and must be created by privileged credentials that can access the guest token 
API endpoint in Superset.

The backend service takes a refresh token, generated by a Superset admin,
uses it to retrieve an access token, then generates a guest token with access
to the specified dashboard.

## How do I use it?

Set the following env vars:

- `SUPERSET_REFRESH_TOKEN`: the string of the token given to you by the admins. This is secret, and should be kept confidential.
- `SUPERSET_DOMAIN`: the base URL to the Superset installation, e.g. `https://superset.agora-lab.w3n.io/`
- `SUPERSET_DASHBOARD_ID`: the ID of your dashboard, retrieved from the 'Embed' action in the Superset UI after you create it.
- `APP_PORT`: the port number of this app. Default is `8080`

If you use the user's data in any way in the dashboard, that can also be specified:

- `GUEST_USERNAME` 
- `GUEST_FIRSTNAME` 
- `GUEST_LASTNAME` 

but note that this will not be verified by the Superset installation and does not correspond to an actual account, it is up to your application.

Run the backend, either by packaging into a container or directly executing the code via the Bazel python plugin (https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/python/)

```shell
bazel run //ns/data-platform/superset/embed-dashboard/backend:main
```
