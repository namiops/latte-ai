# Changelog

## v1.1.0
- export `SERVICE.keycloak-http` and `SERVICE.drako-v1` according to suggestion by solo.io team [reference](https://woven-by-toyota.slack.com/archives/C05KF2X8YLS/p1706827750051769)
- support `workspaceExportTo: 'ALL'` label for export resource outside workspace 
- remove `ExtAuthServer` as it can't be shared across workspace
- add `ExternalService` and `ExternalEndpoint` for Keycloak internal fqdn `id.${cluster_domain}` [iamsprint/5834907116](https://go/iamsprint/5834907116)

## v1.0.0
- Initial Gloomesh workspace project