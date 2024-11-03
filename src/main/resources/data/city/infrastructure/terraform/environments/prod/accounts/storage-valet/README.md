# Storage Valet IaC

See these documents to:
- [Get access and set up AWS profile](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit#heading=h.704zcetca8ji)
- [Apply a change before the pipeline is available](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit#heading=h.tkkf5390xj0e)

## VPC

Agora Infra has created a VPC for this account inside [base](../../base/) folder.
The VPC id is available in [data.tf](./data.tf).

You can refer to the VPC ID by `data.aws_vpc.storage_valet_vpc.id`.
