# Network Load Balancer Configurations

* [Network Load Balancer Configurations](#network-load-balancer-configurations)
  * [Related TF files](#related-tf-files)
  * [Deploy a new load balancer](#deploy-a-new-load-balancer)
    * [1. Prepare a new security group](#1-prepare-a-new-security-group)
    * [2. Create a new Terraform module](#2-create-a-new-terraform-module)
  * [Add a new listener](#add-a-new-listener)


## Related TF files

- DEV: [dev2/base/mlops1_east-lb.tf](../../../terraform/environments/dev2/base/mlops1_east-lb.tf)
- Prod: TBD


## Deploy a new load balancer

### 1. Prepare a new security group

The network connection between NLB and EC2 in VMS accounts is strictly limited by security groups.
When you create a new load balancer, you MUST also create a new security group and associate it with the new NLB.

You can create the new security group by following steps in [Centralized Security Group Configuration](./security-groups.md). It is recommended to name the new security group with `<you_nlb_name>-lb` pattern. For example, `cloudpf-lb`.

### 2. Create a new Terraform module

See the example module `module "mlops1_east_vai_vms_cloudpf_lb"` at [dev2/base/mlops1_east-lb.tf](../../../terraform/environments/dev2/base/mlops1_east-lb.tf)

You can copy the example module and change a key to these 2 configs:

1. A new Security group key from [1. Prepare a new security group](#1-prepare-a-new-security-group)
2. All references to a new listener config
3. Target group prefix
   1. A prefix must be unique between load balancers too.

```tf
module "mlops1_east_vai_vms_awesome_nw" {
  source  = "terraform-aws-modules/alb/aws"
  ...
  # 1. A new Security group key
  security_groups       = [module.mlops1_east_vai_sg["awesome-nw-lb"].security_group_id]


  listeners = {
    # 2. A new listener config
    for key, config in local.mlops1_east_lb_config.awesome_nw_lb.listeners :
    ...
  }

  target_groups = {
    # 2. A new listener config
    for key, config in local.mlops1_east_lb_config.awesome_nw_lb.listeners :
    config.target_group.key => {
      # 3. Target group prefix
      name         = "awesome-nw-lb-${config.target_group.key}"
      ...
    }
  }

  additional_target_group_attachments = tomap({
    for idx, value in flatten([
      # 2. A new listener config
      for key, config in local.mlops1_east_lb_config.awesome_nw_lb.listeners : [
        ...
      ]
    ])
  })
}
```


## Add a new listener

The below steps describe how to add a new listener to an existing load balancer by Terraform. A target group will be added at the same time.

1. Go to the related file per environment listed in [Related TF files](#related-tf-files).
2. Add a new key to a configuration defined per a load balancer as a local variable `mlops1_east_lb_config.<lb_name>.listeners`. For example, to add a new listener called `my-new-listener` to the load balancer `cloudpf_lb`:

```tf
locals {
  mlops1_east_lb_config = {
    cloudpf_lb = {
      name = "agora-vai-vms-cloudpf"
      listeners = {
        my-new-listener = {
          port     = <port_number>
          protocol = <protocol>
          target_group = <TBD>
        }
      }
    }
  }
}
```

**NOTE:** 

- Refer to the available protocols for NLB at [lb_listener doc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener#protocol)
- In the case of `TLS` protocol, you should add `certificate_arn` like the below:

```tf
      listeners = {
        my-new-listener = {
          port     = 443
          protocol = "TLS"
          certificate_arn = <your_certificate_arn>
        }
      }
```

3. Get a list of instance IDs from a Terraform module provisioning EC2. 
For example,

- ONB server: `module.mlops1_east_vai_onb_vm[*].instance_id`
- Management server: `module.mlops1_east_vai_ms_vm[*].instance_id`

4. Update a target group by updating the value of `target_group`. We recommend setting the value of `key` with the pattern `<your_listener_name>-group`. Then, set `target_ids` with the value from the previous step.

**NOTE:** 

- The protocol of a target group here can be `UDP` or `TCP`.
- The protocol of health check is `HTTP` by default. The protocol of health check can be `HTTP` or `TCP`.

For HTTP target group.

```tf
locals {
  mlops1_east_lb_config = {
    cloudpf_lb = {
      name = "agora-vai-vms-cloudpf"
      listeners = {
        my-new-listener = {
          target_group = {
            key        = "my-new-listener-group"
            port       = 80
            protocol   = "TCP"
            target_ids = module.mlops1_east_vai_ms_vm[*].instance_id
            health_check = {
              enabled  = true
              interval = 30
              path     = "/"
              port     = "traffic-port"
              matcher  = "200-399"
            }
          }
        }
      }
    }
  }
}
```

For non-HTTP target group

```tf
locals {
  mlops1_east_lb_config = {
    cloudpf_lb = {
      name = "agora-vai-vms-cloudpf"
      listeners = {
        my-new-listener = {
          target_group = {
            key        = "my-new-listener-group"
            port       = 554
            protocol   = "TCP"
            target_ids = module.mlops1_east_vai_onb_vm[*].instance_id
            health_check = {
              enabled  = true
              interval = 30
              protocol = "TCP"
              port     = "traffic-port"
            }
          }
        }
      }
    }
  }
}
```

5. Update `health_check` to match your requirements such as interval, HTTP path or healthy threshold. See [lb_target_group - health_check](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group#health_check) for more available options
