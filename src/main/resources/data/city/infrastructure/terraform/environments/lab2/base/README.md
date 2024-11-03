## Running terraform commands

Due to this module still being in development, it does not come up and destroy cleanly. Below are some notes to consider

### Running Apply

Steps to follow to apply properly

```bash
# Apply specific resources before others due to bad dependency ordering
terraform apply -auto-approve \
  -target=module.management_east_tgw.module.tgw.aws_ec2_transit_gateway_vpc_attachment.this \
  -target=module.worker1_east_tgw.module.tgw.aws_ec2_transit_gateway_vpc_attachment.this \
  -target=module.worker1_west_tgw.module.tgw.aws_ec2_transit_gateway_vpc_attachment.this
terraform apply -auto-approve
```

### Running Destroy

1. Before running terraform destroy, ensure these resources are deleted. Either manually, or through k8s manifests

- Karpenter nodes
- AWS ELB

2. When destroying, Node Groups can degrade and become stuck. When this happens, delete the node groups manually
