## Setup

- modify `bin/sysdig-deploy.yaml` to include [accessKey](https://docs.sysdig.com/en/docs/administration/administration-settings/user-profile-and-password/retrieve-the-sysdig-api-token/#retrieve-the-sysdig-api-token) and secureAPIToken
  - secureAPIToken is under `https://app.{region}.sysdig.com/secure/#/settings/user`
  - **do not commit and push this file**
- run `import` script i.e. `./import -f sysdig-deploy.yaml -t sysdig-deploy`
- apply the secret (inside bin) after ns creation
- create PR and merge

## References
- https://github.com/sysdiglabs/charts/tree/master/charts/sysdig-deploy
- mt.fuji sysdig-* https://github.tri-ad.tech/TRI-AD/mtfuji-infra/tree/main/manifests
