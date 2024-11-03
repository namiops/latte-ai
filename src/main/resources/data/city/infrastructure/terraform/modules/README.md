# Upload terraform modules to artifactory

To upload terraform modules to artifactory, add `BUILD` file to the directory and add the following target:

```sh
# Package the terraform modules into tar.gz
city_terraform_package(
    name = "module_name",
    srcs = glob(
        ["**"], # files you want to include in the module
        exclude = [
            "BUILD",
        ],
    ),
)

# Upload to agora-tf artifactory
city_terraform_upload(
    name = "upload",
    artifact = ":module_name",
    provider = "{provider}",  # can be ommited because "default" is the default value
    namespace = "agora" # can be ommited because agora is the default value
)
```
It will be uploaded to [agora-tf](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/agora-tf) in artifactory and it requires the module to be uploaded with the following format:

```
{namespace}/{module_name}/{provider}/{version}.zip
```

The version will automatically stamped with `city_semver`, it's using current date as the
major, minor, patch with commit sha as the additional metadata.

Example
```
agora/agora_aws_environment_bootstrap/default/2023.9.27+128410f4a3.zip
```

* notice the double `agora` and `/default` at the end, as we have to follow the Artifactory format.

# Using modules from artifactory

## Configure credentials for terraform

- Open [artifactory ui](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/agora-tf) from browser
- Click `Set Me Up` on the top right corner
- Choose `agora-tf` from Repository selection
- Click `Generate Token & Create Instruction`
- Copy the generated token
- Create `~/.terraform.d/credentials.tfrc.json` on your machine and use this content
  ```json
  {
    "credentials": {
      "artifactory-ha.tri-ad.tech:443": {
        "token": "{token from previous step}"
      }
    }
  }
  ```

## Using the modules
In the module block, replace the source with the appropriate url from artifactory and use the version you want.
```hcl
module "bootstrap" {
  source  = "artifactory-ha.tri-ad.tech/agora-tf__agora/agora_aws_environment_bootstrap/default"
  version = "2023.9.27+128410f4a3"
  ...
}
```

If the module has sub-module and you want to use the sub-module only, use `//` after the prodiver
```hcl
module "mgmt_east_eks" {
  source  = "artifactory-ha.tri-ad.tech/agora-tf__agora/agora_aws_eks_ipv6/default//modules/aws_auth_configmap"
  version = "2023.9.27+128410f4a3"
}
```
