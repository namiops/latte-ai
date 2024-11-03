# External-Authorizer

Called from istio extensionProviders, Provide authentication and authorization.

This was a go implementation set to replace the java-based STS, but ended up
not being used in the end due to a few issues, including:

1. serious security bugs on session management (expiring session didn't really work, same as revoking token).
1. relience on docker-compose for testing instead of integration with our local cluster environment.
1. no support to consent at first; later added in a way that this code only worked either with keycloak authorization
   services or consent cluster-wide, not even by workload.
1. most of the features didn't work as originally designed (including keycloak authz support).


Instead of fixing those issues we decided it would be quicker to develop `drako`, which had already
the design proposed and approved agora-wide.

As of Jan 2023, `drako` is already being used by multiple teams with security review concluded and approved
for exposing to the internet given small changes in logs as requested by SOC team.
