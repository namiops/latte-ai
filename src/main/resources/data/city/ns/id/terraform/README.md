# Identity Terraform

The Identity team provisions infrastructure resources using Terraform.

# Why do we provision our own infrastructure?

Since speedway-{dev, prod} does not provide a database solution (unlike the old Agora infrastructure), each team must manage its own infrastructure resources for database solutions.

## How to apply changes
Please follow the link for each envrionment

- [agora-wovenid-experiments](./environments/agora-wovenid-experiments/README.md)
- [agora-wovenid-dev](./environments/agora-wovenid-dev/README.md)
- [prod-platform-internal](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit#heading=h.tkkf5390xj0e?usp=sharing)


# What are we provisioning?

## WovenID DB
Database for the WovenID service (Keycloak):
- Aurora Postgres Cluster + Network
- RDS Proxy
- Private Link

<div>
  <img
    src="./diagrams/wovenid-db.png"
    alt="WovenID DB Diagram"
  >
</div>

## Grafana Cloud
Observability for WovenID DB:
- Data Source
- Service Account
- Metrics Dashboard
- Logs Dashboard

# Where are we provisioning to?

AWS resources are provisioned on the envrionments below.

Grafana resources are provisioned on our speedway-dev and speedway-prod stack.

[Grafana Cloud Speedway Dev Stack](https://wcmagoradev.grafana.net/)

<div>
  <img
    src="./diagrams/wovenid-db-observability-dev.png"
    width="50%"
    alt="Woven ID Observability Dev Diagram"
  >
</div>

[Grafana Cloud Speedway Prod Stack](https://wcmagoraprod.grafana.net/)

<div>
  <img
    src="./diagrams/wovenid-db-observability-prod.png"
    width="50%"
    alt="Woven ID Observability Prod Diagram"
  >
</div>

# `environments`

The `/environments` directory defines the AWS accounts/environments owned by the Identity team.

As of October 2024, since CI/CD is not set up across all environments, you will need to manually apply any new changes.

## agora-wovenid-experiments

Sandbox AWS account for the Identity team.

## agora-wovenid-dev

Development AWS account for the Identity team.

The resources in this account are used for speedway-dev.

## prod-platform-internal

Production AWS account co-owned by multiple teams, including the Identity team.

The resources in this account are used for speedway-prod.

You can reference the resource provisioning of prod-platform-internal in `CITY_ROOT_DIR/infrastructure/terraform/environments/prod/accounts/platform-internal/id`.

Please check out [the document](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit?usp=sharing) for more details.

# `modules`

## agora_id_wovenid_grafanacloud
Terraform module to provision alerts, logs dashboard, and metrics dashboard for WovenID DB.

## agora_id_wovenid_db

Terraform module to provision the Aurora Postgres cluster, network, RDS proxy, and private link.

`agora_id_wovenid_db` has been promoted to `terraform/modules` as the module has been provision on `prod-platform-internal`.
