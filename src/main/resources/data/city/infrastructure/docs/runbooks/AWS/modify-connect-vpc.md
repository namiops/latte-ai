# Modify Connected VPC

This runbook provides steps to import a Connected VPC (provisioned from https://devops.tri-ad.tech/) and modify it to meet customer requirements.
One of the example use cases is VisionAI VMS's VPC, which requires connecting to EnTec Gen2 Network (for cameras in Nihonbashi office) and provisioning six subnets.
However, the Connected VPC comes with two subnets using all available IP blocks from VPC CIDR range by default.
More details of the above use case can be found in [TN-0456 Agora-to-Nihonshi-office connectivity for VisionAI cameras](https://docs.google.com/document/d/19lEYgfQa0J3GlsUP1RwsAxwEk3tiEZOXkxJ0AGxlVPY/edit)

**NOTE:** See the example IaC at [mlops1_east-vpc.tf](../../../terraform/environments/dev2/base/mlops1_east-vpc.tf)

## Customize subnets

1. Create a Connected VPC from [EnTec DevOpsPortal](https://devops.tri-ad.tech/). In case of `ap-northeast-1`, it comes with 2 default subnets in AZ a & c. They took all available IPs from VPC's CIDR. Let’s call them:
  - DefaultA subnet: a default subnet in AZ a 
  - DefaultC subnet: a default subnet in AZ c
**WARNING:** :warning: DO NOT delete the EnTec-created Transit gateway attachment even though it blocks us from deleting 2 default subnets. Otherwise, you will lose connectivity to EnTec Gen2.

2. Remove DefaultC subnet from the Transit gateway attachment to free the allocated IP blocks.
3. Create new subnets as you need in AZ c.
4. Attach one of new subnets in AZ c to the Transit gateway attachment.
5. Remove DefaultA subnet from the Transit gateway attachment.
6. Create new subnets as you need in AZ a.
7. Attach the new subnet to the Transit gateway attachment.
8. Import VPC-related resources into Terraform state. Make sure to import the Transit gateway attachment to Terraform state. 
   - See the example code in [mlops1_east-vpc.tf](../../../terraform/environments/dev2/base/mlops1_east-vpc.tf) 
