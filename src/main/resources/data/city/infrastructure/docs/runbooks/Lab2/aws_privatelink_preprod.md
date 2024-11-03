# AWS PrivateLink to Pre-prod environment

This document explains the steps to create AWS PrivateLink from customer's AWS service to Agora pre-prod environment.
There are steps to be done on both the customer and Agora sides.

* [AWS PrivateLink to Pre-prod environment](#aws-privatelink-to-pre-prod-environment)
  * [Changes on user side](#changes-on-user-side)
  * [Changes on Agora side](#changes-on-agora-side)


## Changes on user side

1. Create a load balancer for a service that you would like to expose to Agora network. Please refer to [Create a Network Load Balancer](https://docs.aws.amazon.com/elasticloadbalancing/latest/network/create-network-load-balancer.html) for more info.
2. Create a VPC endpoint service mapped to the load balancer
   1. You can refer to the official document at [Create a service powered by AWS PrivateLink](https://docs.aws.amazon.com/vpc/latest/privatelink/create-endpoint-service.html)
3. Allow a principal depending on Agora environments:
  - Lab2/Sandbox (`lab2-transit`): `arn:aws:iam::716975162005:root`  
  - Dev2/Pre-prod (`dev2-transit`): `arn:aws:iam::712742136960:root`


## Changes on Agora side

Users can follow these steps to create a connection from the VPC endpoint service provisioned in the previous section to the Agora pre-prod network. These steps modifies a config at [dev2/base/transit_east-vpc_link.tf](../../../terraform/environments/dev2/base/transit_east-vpc_link.tf).

1. (Optional for a new domain) In `locals.transit_east_vpc_link_config.dns_zones`, append a new config as follows. Please note you need to set the key `<your_domain_without_dot>` by replacing a dot in your domain with an underscore. Ex. `woven-planet.tech` becomes `woven-planet_tech`

```tf
      <your_domain_without_dot> = {
         vpc_id     = module.transit_east_vpc.vpc.vpc_id
         vpc_region = "ap-northeast-1"
         dns_zone   = "<your_domain>"
         associated_vpcs = merge(
            { for name, vpc in local.east_vpcs : name => { id = vpc.vpc_id, region = "ap-northeast-1", name = name } if name != "transit-east" },
         )
      }   
```

For example,

```tf
      woven-planet_tech = {
        vpc_id     = module.transit_east_vpc.vpc.vpc_id
        vpc_region = "ap-northeast-1"
        dns_zone   = "woven-planet.tech"
        associated_vpcs = merge(
          { for name, vpc in local.east_vpcs : name => { id = vpc.vpc_id, region = "ap-northeast-1", name = name } if name != "transit-east" },
        )
      }
```

2. Create a new module called `vpc_endpoint_east_<your_team>_<name>` with the following example. Please note that the number of port ranges can be up to 2 per security group in pre-prod. The reason is that an ingress rule of 1 port range will be generated per CIDR block (total of 30 blocks in pre-prod) and AWS limits 60 rules per security group (hard limit). 
   
```tf
module "vpc_endpoint_east_<your_team>_<name>" {
  source = "../../../modules/agora_aws_nextgen_vpc_endpoint"
  providers = {
    aws = aws.transit-east
  }
  vpc_id             = module.transit_east_vpc.vpc.vpc_id
  subnet_ids         = module.transit_east_vpc.vpc.private_subnets
  source_cidr_blocks = flatten([for _, vpc in local.internal_vpcs : vpc.private_subnets_cidr_blocks])

  # Modify these variables
  name               = "<your_team>-<name>"
  service_name       = "<vpc_endpoint_service_name>"
  azs                = [<az_of_vpc_endpoint_service>]
  security_group_allowed_ports = {
    <security_group_1> = {
      port_ranges = [<port_range>]
    }
    <security_group_2> = {
      port_ranges = [<port_range>]
    }
  }
}
```

For example,
```tf
module "vpc_endpoint_east_awesome_lapras_cloudamqp" {
  source = "../../../modules/agora_aws_nextgen_vpc_endpoint"
  providers = {
    aws = aws.transit-east
  }
  vpc_id             = module.transit_east_vpc.vpc.vpc_id
  subnet_ids         = module.transit_east_vpc.vpc.private_subnets
  source_cidr_blocks = flatten([for _, vpc in local.internal_vpcs : vpc.private_subnets_cidr_blocks])
  
  name               = "Awesome Lapras CloudAMQP"
  service_name       = "com.amazonaws.vpce.ap-northeast-1.vpce-svc-0123456789"
  azs                = ["ap-northeast-1a", "ap-northeast-1c"]
  security_group_allowed_ports = {
    https_amqp = {
      port_ranges = [
        [443, 443], [5671, 5672]
      ]
    }
    stream_stomp = {
      port_ranges = [
        [5551, 5552], [61613, 61614]
      ]
    }
    mqtt = {
      port_ranges = [
        [1883, 1883], [8883, 8883]
      ]
    }
  }
}
```

**NOTE:** Make sure to modify `azs = [<az_of_vpc_endpoint_service>]` with the AZs you used to create the VPC endpoint service in [User side steps](#user-side). 

3. In `module.transit_east_vpc_link`, append a new config to `shared_services` attribute.

```tf
    <your_fqdn_without_dot> = {
      service_name = module.<module_name_from_step_2>.vpc_endpoint_dns_name
      dns_name     = "<your_fqdn>"
      dns_zone     = "<your_domain_without_dot>"
    }
```

For example,

```tf
    awesome-lapras_woven-planet_tech = {
      service_name = module.vpc_endpoint_east_awesome_lapras_cloudamqp.vpc_endpoint_dns_name
      dns_name     = "awesome-lapras.woven-planet.tech"
      dns_zone     = "woven-planet_tech"
    }
```

4. Create a pull request and send it to Agora Infra team for review. You can refer to this PR as an example: https://github.com/wp-wcm/city/pull/31384.

5. In case the `Acceptance required` option is enabled at your VPC endpoint, go to your AWS console to accept the connection request after the PR has been merged and Agora CD pipeline has applied the change.
