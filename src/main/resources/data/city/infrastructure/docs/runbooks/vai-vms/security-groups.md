# Centralized Security Group Configuration

The inbound network traffic in VMS accounts is strictly controlled by Security Group (SG). We MUST set inbound rules with a specific source. The source can be another SG or CIDR block.

* [Centralized Security Group Configuration](#centralized-security-group-configuration)
  * [Related files](#related-files)
  * [Create a new security group](#create-a-new-security-group)
  * [Add new ingress rules](#add-new-ingress-rules)

## Related files

- DEV: 
  - Config: [dev2/base/mlops1_east_configs/security_groups.yaml](../../../terraform/environments/dev2/base/mlops1_east_configs/security_groups.yaml)
  - Terraform code: [dev2/base/mlops1_east-ec2.tf](../../../terraform/environments/dev2/base/mlops1_east-ec2.tf)
- Prod: TBD

## Create a new security group

**NOTE:** The default egress rule allows traffic to any ports and destinations.

1. Go to the YAML config file per environment listed in [Related files](#related-files).
2. Create a security group by adding a new key. For example, `ollie`.
3. (Optional) If you don't plan to set up ingress rules, add n rule with an empty list like below.

```yaml
ollie:
  sg_ingress_rules: []
```

## Add new ingress rules

1. Add ingress rules for traffic from specific SGs by adding a new key in `sg_ingress_rules`. It's recommended to name a key with `<sg_source_key>_<port_range>` to make the key unique.
   1. `sg_source_key`: a key of an SG defined in this YAML config.
   2. `description`: a description of an ingress rule.
   3. `port_range`: a pair of a port range allowed by SG. If there is only one port, set both values with the same port.

```yaml
ollie:
  sg_ingress_rules:
    mos_443:
      sg_source_key: mos
      description: "Mobile servers port 443"
      port_range: [443, 443]
    ms_9001_9005:
      sg_source_key: ms
      description: "Management server 9001-9005"
      port_range: [9001, 9005]
mos:
  # Omit configs
  # ...
ms:
  # Omit configs
  # ...
```

2. Add ingress rules for traffic from the specific CIDR block by adding a new key in `cidr_ingress_rules`. It's recommended to name a key with `<traffic_source_name>_<port_range>` to make the key unique.
   1. `ipv4`: a list of IPv4 CIDR blocks to be allowed.
   2. `description`: a description of an ingress rule.
   3. `port_range`: a pair of a port range allowed by SG. If there is only one port, set both values with the same port.

For example, the below IPv4 CIDR block is used by CloudPF services running on SMC.
```yaml
cloudpf-lb:
  cidr_ingress_rules:
    cloudpf_443:
      ipv4: ["10.0.0.0/8"]
      description: "HTTPS connection from VAI CloudPF"
      port_range: [443, 443]
```
