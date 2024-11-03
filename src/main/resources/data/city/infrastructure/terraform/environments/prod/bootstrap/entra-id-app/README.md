## Add Microsoft Entra ID application

* We currently have to `terragrunt apply` manually for now

For more information https://learn.microsoft.com/en-us/cli/azure/authenticate-azure-cli-service-principal

* Make sure you have `az` (Azure CLI) installed

* Grab the client ID, client secret and tenant ID from [Vault](https://vault.tmc-stargate.com/) from namespace `ns_stargate/ns_prod_agorainfra` and path `kv-prod/azure/tf-azure-client`

```bash
az login --service-principal -u <aad_client_id> -p <aad_client_secret> --tenant <aad_tenant_id>
```

**NOTE:** If you encounter this error `ValueError: allow_broker=True is only supported in PublicClientApplication`, run the following command ([Ref](https://github.com/Azure/azure-cli/issues/26052#issuecomment-1499886237)):

```bash
az config unset core.allow_broker
```

Once you have signed in using a service principal, you might see something similar to the below

```bash
[
  {
    "cloudName": "AzureCloud",
    "homeTenantId": "00000000-0000-0000-00000000000000000",
    "id": "00000000-0000-0000-0000-000000000000",
    "isDefault": true,
    "managedByTenants": [],
    "name": "Microsoft Azure エンタープライズ",
    "state": "Enabled",
    "tenantId": "00000000-0000-0000-0000-000000000000",
    "user": {
      "name": "00000000-0000-0000-0000-000000000000",
      "type": "servicePrincipal"
    }
  }
]
```

* You can then `terragrunt apply`

* Once applied, check that your application has been successfully created https://portal.azure.com/#view/Microsoft_AAD_IAM/ActiveDirectoryMenuBlade/~/RegisteredApps
