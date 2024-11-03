# General requirement


# Transit network

VPC CIDR /21 - 10.220.0.0/21 ap-

3 Private subnets /23

# Bastion hosts

Bastion host networks use GEN2 vpc subnets and do not require any additional allocation

# Management Cluster requirements

VPC CIDR /21

3 public subnets at /24
3 private subnets at /24

# Management Cluster apne1


# Management Cluster apne3


# Workload Cluster requirements

Workload clusters should be configured to enable maximum scalability in 3 azs

Workload cluster is /19

3 Public subnets at /24

3 Private subnets for EKS each at /21

3 Private subnets for AWS services at /24

2 unused /24 remaining

## Workload Cluster apne1


## Workload Cluster apne3


## Table format


| **Network**     | **Address**   | **Broadcast** | **Direction**  | **Name** |
| ---------------:| -------------:|:-------------:| -------------- | -------- |
|   10.220.0.0/16 |  10.0.255.255 | User-Facing   |                |          |
|   10.220.0.0/19 |   10.0.31.255 | User-Facing   |     management |  apne1   |
|   10.220.0.0/21 |    10.0.7.255 | Management    |        transit |  apne1   |
|   10.220.8.0/21 |   10.0.15.255 | User-Facing   | management k8s |  apne1   |
|  10.220.32.0/19 |   10.0.63.255 | User-Facing   |       workload |  apne1   |
| 10.220.128.0/19 |  10.0.159.255 | User-Facing   |                |          |
| 10.220.128.0/21 |  10.0.135.255 | User-Facing   |        transit |  apne3   |
| 10.220.136.0/21 |  10.0.143.255 | User-Facing   | management k8s |  apne3   |
| 10.220.160.0/19 |  10.0.191.255 | User-Facing   |      workload1 |  apne3   |
