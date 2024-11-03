# Importing Data from an S3 Bucket into Databricks

## Basic Concepts

### Storage Credential
In Databricks, a storage credential is a secure way to access external storage resources. It ensures that your Databricks environment can read from and write to external data stores like S3, while maintaining security and compliance.

### External Location
An external location in Databricks is a logical pointer to a storage location outside of the Databricks workspace. This setup allows you to manage permissions and access controls for data stored in S3. All metadata is still controlled in Unity Catalog, and you retain ownership over your own data.

## Step-by-Step Guide

For full details, see [https://docs.databricks.com/en/connect/unity-catalog/storage-credentials.html](this Databricks article). The steps here are largely the same, with some additional clarity or caution added and some unnecessary detail removed.

### Step 1: Setting Up AWS IAM Roles and Policies
Before configuring Databricks, ensure that you have the necessary IAM roles and policies in AWS to allow access to your S3 bucket. 

1. **Create an IAM Role**: Navigate to the IAM console in AWS and create a new role that will allow access to the bucket. 

Role creation is a two-step process. The role must be self-assuming (able to trust itself), and the role itself must exist to set up that self-trust policy. In order to achieve this, create the role and add a temporary trust relationship policy and a placeholder external ID, then modify it after creating the storage credential in Databricks.

Create the role with the following trust policy. This should be copied and pasted exactly, as the principal ARN is important:

```
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "AWS": [
        "arn:aws:iam::414351767826:role/unity-catalog-prod-UCMasterRole-14S5ZJVKOTYTL"
      ]
    },
    "Action": "sts:AssumeRole",
    "Condition": {
      "StringEquals": {
        "sts:ExternalId": "0000"
      }
    }
  }]
}
```

The ARN `arn:aws:iam::414351767826:role/unity-catalog-prod-UCMasterRole-14S5ZJVKOTYTL` is a Databricks-created principal that allows Databricks access to the bucket. The policy sets the external ID to 0000 as a placeholder and will be changed to the external ID of your storage credential later.


2. **Create an IAM Policy**: Create a new IAM policy to grant access to the bucket and attach it to the role. 

Note that:

* The KMS section can be removed if your S3 bucket is not using KMS keys.

* This IAM policy grants read and write access. Read-only is also acceptable at the IAM policy level, but the storage credential can also be marked read-only and the write access granted by this IAM role will be ignored by Databricks.

* This policy is more flexible than the trust policy and the permissions can be adjusted to your specific use case.

* The `AWS-ACCOUNT-ID` is the account containing your S3 bucket, not the Databricks AWS account.

Example policy: 
```
{
  "Version": "2012-10-17",
  "Statement": [
      {
          "Action": [
              "s3:GetObject",
              "s3:PutObject",
              "s3:DeleteObject",
              "s3:ListBucket",
              "s3:GetBucketLocation"
          ],
          "Resource": [
              "arn:aws:s3:::<BUCKET>/*",
              "arn:aws:s3:::<BUCKET>"
          ],
          "Effect": "Allow"
      },
      {
          "Action": [
              "kms:Decrypt",
              "kms:Encrypt",
              "kms:GenerateDataKey*"
          ],
          "Resource": [
              "arn:aws:kms:<KMS-KEY>"
          ],
          "Effect": "Allow"
      },
      {
          "Action": [
              "sts:AssumeRole"
          ],
          "Resource": [
              "arn:aws:iam::<AWS-ACCOUNT-ID>:role/<AWS-IAM-ROLE-NAME>"
          ],
          "Effect": "Allow"
      }
    ]
}
```

### Step 2: Databricks setup
   
1. **Create a Storage Credential**:
   Open the Catalog page from the Databricks sidebar menu, then open the 'External data' menu item at the top of the page and then the 'Credentials' tab. Select 'Create credential', 'AWS IAM Role', then paste the ARN of the role you created in Step 1.

   After the credential is created, copy and paste the External ID from the dialog box back into the trust policy for the role in the AWS IAM console.

   Add the IAM Role ARN to the Allow section to allow the role to self-assume. 

   Your policy should now look like this:

   ```
   {
    "Version": "2012-10-17",
    "Statement": [
        {
        "Effect": "Allow",
        "Principal": {
            "AWS": [
            "arn:aws:iam::414351767826:role/unity-catalog-prod-UCMasterRole-14S5ZJVKOTYTL",
            "arn:aws:iam::<YOUR-AWS-ACCOUNT-ID>:role/<THIS-ROLE-NAME>"
            ]
        },
        "Action": "sts:AssumeRole",
        "Condition": {
            "StringEquals": {
            "sts:ExternalId": "<STORAGE-CREDENTIAL-EXTERNAL-ID>"
            }
        }
        }
    ]
    }
   ```

2. **Restrict access**:
   By default, every Databricks user in every workspace will have access to your storage credential. Uncheck 'All workspaces have access' and bind the credential to only the workspaces that need access.

### Step 3: Creating an External Location with your Storage Credential in Databricks
   Open the Catalog page from the Databricks sidebar menu, then open the 'External data' menu item at the top of the page and then 'Create Location' and select 'Manual'.

   Name the credential something meaningful, then select the Storage Credential you just created and specify the URL for your S3 bucket.

   After creation, Databricks automatically runs preliminary checks: if there are any issues with the policy, trust relationship, or IAM role, a dialog box will appear showing which test failed.

### Step 4: Creating a managed table from your data

    Databricks now has access to your files stored in S3. To import these to your workspace's managed storage in a tabular format, Databricks provides native S3 integration. While complicated cases may require a custom notebook to handle the data import, most scenarios can be handled directly via the Databricks UI.

    Table import requires a SQL Warehouse to operate, which is outside the scope of this document.

    On the sidebar menu, select 'Data ingestion' and then the Amazon S3 integration.

    From the dropdown menu, select the External Location you just created, then the files you want to create tables from. Click 'Preview table'.

    Select the catalog (most likely your workspace's primary catalog) to add the data to, then create a new schema (or use an existing one) and specify the destination table. 

    Click 'Create table' and your data will be imported and usable just like any other Unity Catalog data.

    