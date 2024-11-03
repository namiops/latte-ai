# Tenant creation

## What?

This is a Pulumi tenant creation program.

## Why?

This program will create all necessary resources for a new IoTA tenant.

## How?

This program will be located and executed by the onboarding service.

Add your tenant to a file named `Pulumi.<ENV>_<TENANT-NAME>.yaml`. 

For example, if your tenant is named `sample` and your target environment is `prod`, add a similar structure to `Pulumi.prod_sample.yaml`:
```yaml
config:
  sample:users:
    - jdoe  
```

Optionally you may request a code ownership for your file to add additional users without the Services Team approval later.

### When adding users to your tenant please make sure that they exist in keycloak

For example, if you are adding yourself to the production environment, go to https://id.woven-city.toyota/auth/realms/woven/account/#/personal-info and check your `Username` field to ensure a successful update.
