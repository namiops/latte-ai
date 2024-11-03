# Terraform Migration Helper (Template) for Azure AD
A Python script here serves as a template file for developing a tool working with Azure API.
It was used when the Agora Infra area migrated existing Azure resources (ex. Azure App Role Assignments) into Terraform code.
You can adapt the script to another use case.

## Features
- Verify app role assginments
- Generate `terraform import` statement for app role assginments

## Installation and setup
- Refer to https://learn.microsoft.com/en-us/graph/sdks/sdk-installation?context=graph%2Fapi%2F1.0&view=graph-rest-1.0#install-the-microsoft-graph-python-sdk-preview
```
pip install -r requirements.txt
```
- Create your data in `aad_data.py`. For example:
```
TENANT_ID = "aaa-bbb"
APP_ID = "ccc-ddd"

LPS_TENANT_GROUP_IDS = ["eee-fff"]
TENANT_GROUP_IDS = ["eee-fff"]
PLATFORM_GROUP_IDS = ["eee-fff"]
```

## Usage
You can run the script by.
```
python main.py
```

**NOTE:** Currently, argument parser not implemented so the script runs every feature. You can comment out some functions in these lines:
```
if __name__ == "__main__":
    verify_aad_group_ara_tenant()
    gen_import_ara_platform_and_tenant()
```
