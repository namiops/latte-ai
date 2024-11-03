## Requirements

No requirements.

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_lb.msk-broker-nlb](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb) | resource |
| [aws_lb_listener.msk-broker-listener-sasl](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_listener.msk-broker-listener-tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener) | resource |
| [aws_lb_target_group.msk-broker-tg](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group) | resource |
| [aws_lb_target_group_attachment.msk-broker-tg-attachment-sasl](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_lb_target_group_attachment.msk-broker-tg-attachment-tls](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment) | resource |
| [aws_vpc_endpoint_service.msk-broker-service](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc_endpoint_service) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_allowed_principal"></a> [allowed\_principal](#input\_allowed\_principal) | n/a | `string` | `"arn:aws:iam::751698529662:root"` | no |
| <a name="input_broker_list"></a> [broker\_list](#input\_broker\_list) | n/a | <pre>list(<br>    object({<br>      name   = string<br>      az     = string<br>      ip     = string<br>      subnet = string<br>    })<br>  )</pre> | <pre>[<br>  {<br>    "az": "ap-northeast-1d",<br>    "ip": "10.13.202.22",<br>    "name": "msk-broker-1",<br>    "subnet": "subnet-055742bca8ae94fb9"<br>  },<br>  {<br>    "az": "ap-northeast-1c",<br>    "ip": "10.13.201.112",<br>    "name": "msk-broker-2",<br>    "subnet": "subnet-0cff5e7fa560b04fd"<br>  },<br>  {<br>    "az": "ap-northeast-1a",<br>    "ip": "10.13.200.189",<br>    "name": "msk-broker-3",<br>    "subnet": "subnet-0cfa29a3099cfa609"<br>  }<br>]</pre> | no |
| <a name="input_vpc_id"></a> [vpc\_id](#input\_vpc\_id) | n/a | `string` | `"vpc-0220ad7e03b1dd698"` | no |

## Outputs

No outputs.
