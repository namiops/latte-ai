# Decrypt Admin Password

Decrypt the Administrator password for each Windows EC2 created by Agora module `agora_aws_vai_vms_vm`.

## 1 Pre-requisites

- `jq` tool
- `aws-cli`

For the default setting:
- Access to the Terraform state on `dev2-transit` account
- Access to resources on `dev2-mlops1-east` account

## 2 Script

1. For another environment, modify the below variables 

  -  `TF_STATE_AWS_PROFILE`
  -  `TF_STATE_S3_PATH`
  -  `EC2_AWS_PROFILE`

2. Make sure to run `aws sso login --profile <profile>` for two profiles set above.
   
3. Modify a script by adding your target instances.

*TODO:* Improve this by iterating over a map.

```bash
get_vm_password <ec2_name> <tf_module_name[index]>
```

Example

```bash
get_vm_password "agora-vai-onb-vm-000" "mlops1_east_vai_onb_vm[0]"
```

3. Run the script. It generates `output.csv` with [the 1Password-ready CSV format](https://support.1password.com/cs/import-mac/).

```bash
./pw-decryptor.sh -e <ENV>
```

For DEV

```bash
./pw-decryptor.sh -e dev
```

For PROD

```bash
./pw-decryptor.sh -e prod
```

4. Import `output.csv` to [1Password Vault - Agora x VisionAI AWS](https://wovenbytoyota.1password.com/vaults/tlucn6azkadm22mr5gmjviaxse/allitems/423iqgyu2zwgoaiywgdgzeer4e). For the first row, select `Ignore Row` instead of `Login` to ignore the header row.

**NOTE:** Note that 1Password imports each entry without checking duplicated entries. Then, make sure to remove any duplicate existing entry on 1Password Vault, before import.
