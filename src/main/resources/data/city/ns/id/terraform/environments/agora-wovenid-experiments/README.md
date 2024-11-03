## Prerequisite

Please install [terragrunt](https://github.com/gruntwork-io/terragrunt)


## Set AWS environment variables

```shell
export AWS_ACCESS_KEY_ID="xxxxxxxx"
export AWS_SECRET_ACCESS_KEY="xxxxxxx"
export AWS_SESSION_TOKEN="xxxxxxx"
```

## Login vault

```shell
unset VAULT_NAMESPACE
vault login -method=oidc -path=gac
export VAULT_NAMESPACE="ns_stargate/ns_dev_agoraid"
```

## Running terragrunt for the first time

```
terragrunt init
```

## How to apply resources defined by terragrunt

- Create PR and paste the outputs of `terragrunt plan`
- After PR is merged into main, run `terragrunt apply`
