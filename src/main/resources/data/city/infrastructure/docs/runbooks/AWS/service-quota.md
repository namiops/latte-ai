# Update AWS service quota per region

For each service, AWS has limited the number of objects we can provision.
Some service quota is very low. For example, the quota of G-type EC2 instances. 
We can increase this number by Terraform per region.

## Increase the quota with Terraform

1. Search for a service code by running `aws service-quotas list-services` command.
For example, the output should look like below.

```json
        {
            "ServiceCode": "ebs",
            "ServiceName": "Amazon Elastic Block Store (Amazon EBS)"
        },
        {
            "ServiceCode": "ec2",
            "ServiceName": "Amazon Elastic Compute Cloud (Amazon EC2)"
        }
```

2. Get a quota code from one of these options: 
  
  - From [AWS Service Quotas - Dashboard](https://console.aws.amazon.com/servicequotas/home).
  - Find it from AWS CLI. For example, list all service quotas of EC2. The quota code is at `QuotaCode` key.
  
  ```bash
  $ aws service-quotas list-service-quotas --service-code ec2
  ```
  ```json
          {
            "ServiceCode": "ec2",
            "ServiceName": "Amazon Elastic Compute Cloud (Amazon EC2)",
            "QuotaArn": "arn:aws:servicequotas:ap-northeast-1:835215587209:ec2/L-DB2E81BA",
            "QuotaCode": "L-DB2E81BA",
            "QuotaName": "Running On-Demand G and VT instances",
            "Value": 328.0,
            "Unit": "None",
            "Adjustable": true,
            "GlobalQuota": false,
            ...
          }
  ```

3. Create a new Terraform resource in `quota.tf`. If you update a service quota in DEV, you should update `infrastructure/terraform/accounts/835215587209/base/quota.tf`. For example, the new `aws_servicequotas_service_quota` resource should look like the below:

```terraform
resource "aws_servicequotas_service_quota" "ondemand_g_and_vt_instances" {
  quota_code   = "L-DB2E81BA"
  service_code = "ec2"
  value        = 328
}
```

4. After the new Terraform is applied, it takes not more than 30 minutes until the quota request is approved and the quota is changed.
You can track the status of the request at `Pending service quota requests` in [AWS Service Quotas - Dashboard](https://console.aws.amazon.com/servicequotas/home)
